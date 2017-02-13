package com.krashidbuilt.api.data;

import com.krashidbuilt.api.util.DateTime;
import com.krashidbuilt.api.util.ObjectMapper;
import com.krashidbuilt.api.model.ApplicationUser;
import com.krashidbuilt.api.model.Authentication;
import com.krashidbuilt.api.model.Detail;
import com.krashidbuilt.api.model.Error;
import com.krashidbuilt.api.model.ThrowableError;
import com.krashidbuilt.api.service.Encryption;
import com.krashidbuilt.api.service.MySQL;
import com.krashidbuilt.api.service.Settings;
import com.krashidbuilt.api.validation.CustomValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public final class AuthenticationData {

    private static final String SECRET = Settings.getStringSetting("jwt.secret");
    private static Logger logger = LogManager.getLogger();
    private AuthenticationData() {

    }

    public static ApplicationUser login(String email, String password) throws ThrowableError {

        List<Detail> errorDetails = new ArrayList<Detail>();

        if (CustomValidator.isBlank(email)) {
            errorDetails.add(new Detail("Email address is a required field and cannot be empty.", "email", email));
        }

        if (CustomValidator.isBlank(password)) {
            errorDetails.add(new Detail("Password is a required field and cannot be empty.", "password", password));
        }

        if (errorDetails.size() >= 1) {
            Error error = Error.unauthorized();
            error.setBothMessages("Authentication failed, field requirements not met.");
            error.setDetails(errorDetails);
            throw new ThrowableError(error);
        }


        logger.debug("LOGIN USER " + email);

        ApplicationUser user = new ApplicationUser();
        Authentication auth = new Authentication();

        String hash = null;
        boolean valid = false;
        MySQL db = new MySQL();

        try {
            db.setpStmt(db.getConn().prepareStatement("SELECT * FROM application_user WHERE email = ?"));
            db.getpStmt().setString(1, email);
            db.setRs(db.getpStmt().executeQuery());


            while (db.getRs().next()) {
                user = ObjectMapper.applicationUser(db.getRs());
                hash = db.getRs().getString("password");
                auth.fromUser(user);
            }


        } catch (SQLException ex) {
            logger.error("UNABLE TO LOGIN USER " + email, ex);
        }
        db.cleanUp();


        if (hash != null) {
            try {
                valid = Encryption.validatePassword(password, hash);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                logger.error("UNABLE TO VALIDATE HASH AGAINST PASSWORD", e);
            }
        }


        if (!valid) {
            Error error = Error.unauthorized();
            error.setBothMessages("Authentication failed, email or password is incorrect.");
            error.setDetails(
                    new Detail("Email or password is incorrect.",
                            "[\"email\",\"password\"]",
                            "[\"" + email + "\",\"" + password + "\"]"
                    ).toList()
            );
            throw new ThrowableError(error);
        }

        generateTokens(auth, user);

        return user;
    }

    /**
     * Tries to parse specified String as a JWT token.
     * If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the Authentication object extracted from specified token or null if a token is invalid.
     */
    public static Authentication parseToken(String token, TokenType tokenType)
            throws ThrowableError, ExpiredJwtException {
        Claims body = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();

        Authentication auth = Authentication.fromString(body.get("auth").toString());

        if (tokenType == TokenType.ACCESS && !CustomValidator.isBlank(auth.getToken().getAccessToken())) {
            logger.info("USER IS TRYING TO AUTHENTICATE WITH REFRESH TOKEN");
            Error error = Error.unauthorized();
            error.setDetails(new Detail("Access token is expired or is NOT valid.", "access_token", token).toList());
            throw new ThrowableError(error);
        }

        if (tokenType == TokenType.REFRESH && CustomValidator.isBlank(auth.getToken().getAccessToken())) {
            logger.info("USER IS TRYING TO REFRESH WITH AN ACCESS TOKEN");
            Error error = Error.unauthorized();
            error.setDetails(new Detail("Refresh token is expired or is NOT valid.", "refresh_token", token).toList());
            throw new ThrowableError(error);
        }

        return auth;

    }

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims.
     * These properties are taken from the specified
     * User object. Tokens validity is infinite.
     *
     * @param auth the user Auth object for which the token will be generated
     * @return the JWT token
     */
    public static String generateJwt(Authentication auth, TokenType tokenType) {

        int expiration = Settings.getIntSetting("jwt.accessTokenExpiresSeconds");
        if (tokenType == TokenType.REFRESH) {
            expiration = Settings.getIntSetting("jwt.refreshTokenExpiresSeconds");
        }
        if (tokenType == TokenType.EXPIRED) {
            expiration = 0;
        }

        int ttlMillis = expiration * 1000;

        long nowMillis = DateTime.getEpochMillis();

        Claims claims = Jwts.claims().setSubject(auth.getEmail());
        claims.put("auth", auth.toString());


        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId("jwt")
                .setClaims(claims)
                .setIssuedAt(DateTime.fromMillis(nowMillis))
                .setSubject(auth.getEmail())
                .setIssuer(Settings.getStringSetting("jwt.issuer"))
                .signWith(SignatureAlgorithm.HS512, SECRET);
        builder.setExpiration(DateTime.fromMillis(nowMillis + ttlMillis));

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();

    }

    /**
     * Creates a jwt token from the Authentication object. (keep it smaller rather than using the user object)
     * It will update the the user application object after it has been generated.
     *
     * @param auth the auth object for which the token will be generated from
     * @param user the application user object that will contain the token after it is generated
     */
    public static void generateTokens(Authentication auth, ApplicationUser user) {

        auth.getToken().setAccessToken(null);
        auth.getToken().setRefreshToken(null);

        auth.getToken().setAccessToken(generateJwt(auth, TokenType.ACCESS));
        auth.getToken().setRefreshToken(generateJwt(auth, TokenType.REFRESH));

        auth.getToken().setAccessExpires(DateTime.getEpochMillis()
                + (Settings.getIntSetting("jwt.accessTokenExpiresSeconds") * 1000 - 10000));
        auth.getToken().setRefreshExpires(DateTime.getEpochMillis()
                + (Settings.getIntSetting("jwt.refreshTokenExpiresSeconds") * 1000 - 10000));

        user.setApplicationToken(auth.getToken());

        logger.debug("Current = " + DateTime.fromMillis(DateTime.getEpochMillis()).toString());
        logger.debug("Access expires = " + DateTime.fromMillis(auth.getToken().getAccessExpires()).toString());
        logger.debug("Refresh expires = " + DateTime.fromMillis(auth.getToken().getRefreshExpires()).toString());
    }

    public enum TokenType {
        ACCESS, REFRESH, EXPIRED
    }

}
