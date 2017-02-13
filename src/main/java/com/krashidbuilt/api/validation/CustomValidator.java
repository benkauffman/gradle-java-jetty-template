package com.krashidbuilt.api.validation;


import com.krashidbuilt.api.service.Settings;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public final class CustomValidator {

    private static Logger logger = LogManager.getLogger();

    private CustomValidator() {
    }

    public static boolean isInvalidEmail(String email) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        return !emailValidator.isValid(email);
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || value.trim().length() <= 0;
    }


    public static boolean isInvalidLength(String value, int min, int max) {
        if (isBlank(value)) {
            return true;
        }
        String trimmed = value.trim();
        if (trimmed.length() < min) {
            logger.debug("string does not meet minimum length requirements.");
        }

        if (trimmed.length() > max) {
            logger.debug("string does not meet maximum length requirements.");
        }

        return !(trimmed.length() >= min && trimmed.length() <= max);
    }


    public static boolean isInvalidZip(int zip) {
        String zipStr = String.valueOf(zip);
        return isBlank(zipStr) || isInvalidLength(zipStr, 5, 5);
    }

    public static boolean isInvalidDate(String dateStr) {
        DateValidator dateValidator = DateValidator.getInstance();
        return !dateValidator.isValid(dateStr, "yyyy-MM-dd");
    }

    public static boolean isInvalidPassword(String password) {
        if (isBlank(password)) {
            logger.debug("USER PASSWORD CANNOT BE BLANK");
            return true;
        }

        int min = Settings.getIntSetting("validation.password.min");
        int max = Settings.getIntSetting("validation.password.max");

        return isInvalidLength(password, min, max);
    }

    public static boolean isInvalidNumber(String str) {
        return !NumberUtils.isNumber(str);
    }

    public static boolean isInvalidBool(String str) {

        return isBlank(str) ||
                !("false".equalsIgnoreCase(str) || "true".equalsIgnoreCase(str) || "0".equals(str) || "1".equals(str));
    }

}
