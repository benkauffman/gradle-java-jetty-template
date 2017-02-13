package com.krashidbuilt.api.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public final class CacheService {

    private static Logger logger = LogManager.getLogger();
    private static CacheManager cacheManager;

    private CacheService() {

    }

    private static Cache cache() {

        //initialize cache manager if null
        if (cacheManager == null) {
            logger.debug("INITIALIZE NEW CACHE MANAGER");
            cacheManager = CacheManager.newInstance();
        }

//        //Initialise a cache if it does not already exist
//        if (cacheManager.getCache("CacheTest") == null) {
//            logger.debug("INITIALIZE NEW CACHE");
//            cacheManager.addCache("CacheTest");
//        }

        return cacheManager.getCache("CacheTest");
    }


    public static String storeCache(Object obj, int id) {
        return storeCache(obj, String.valueOf(id));
    }

    public static String storeCache(Object obj, String id) {
        String md5 = storeCache(obj);

        if (md5 != null) {
            cache().put(new Element(getObjectId(obj, id), md5));
        }

        return md5;
    }

    public static String storeCache(Object obj) {
        String key = null;
        try {
            key = getMd5Digest(serialize(obj));
            cache().put(new Element(key, obj));
        } catch (IOException e) {
            logger.error("UNABLE TO SERIALIZE AND STORE OBJECT IN CACHE");
        }

        return key;
    }


    public static Object getObjectId(Object obj, int id) {
        return getObjectId(obj, String.valueOf(id));
    }

    private static String getObjectId(Object obj, String id) {
        String className = obj.getClass().getName();
        String[] classNameArr = className.split(Pattern.quote("."));
        if (classNameArr.length >= 1) {
            className = classNameArr[classNameArr.length - 1];
        }

        return className + "||" + id;
    }


    public static Object getCache(Object obj, int id) {
        return getCache(obj, String.valueOf(id));
    }

    public static Object getCache(Object obj, String id) {
        String objId = getObjectId(obj, id);
        logger.debug("GET CACHE KEY WITH OBJECT ID {} ", objId);

        String cacheKey = (String) getCache(objId);
        if (cacheKey != null) {
            logger.debug("CACHE KEY EXISTS {}", cacheKey);
            return getCache(cacheKey);
        }

        return null;
    }


    public static Object getCache(String key) {
        Element element = cache().get(key);
        return (element == null) ? null : element.getObjectValue();
    }

    public static void deleteCache(Object obj, int id) {
        deleteCache(obj, String.valueOf(id));
    }

    public static void deleteCache(Object obj, String id) {
        String objId = getObjectId(obj, id);
        deleteCache(objId);
    }

    public static void deleteCache(String key) {
        //if this is an object reference (class + id)
        //we need to get the hash key so we can delete that along with the hash key/value

        try {
            String md5 = (String) getCache(key);
            if (md5 != null) {
                logger.debug("THIS IS AN OBJECT KEY {}, MD5 IS {}", key, md5);
                cache().remove(md5);
            }
        } catch (ClassCastException ex) {
            logger.debug("THIS IS NOT AN OBJECT KEY IT IS THE MD5", ex);
        }

        //delete the original key/value
        cache().remove(key);
    }

    public static byte[] serialize(Object obj) throws IOException {
        byte[] byteArray = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream out = null;
        try {
            // These objects are closed in the finally.
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(obj);
            byteArray = baos.toByteArray();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return byteArray;
    }

    public static String getMd5Digest(byte[] bytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 cryptographic algorithm is not available.", e);
        }
        byte[] messageDigest = md.digest(bytes);
        BigInteger number = new BigInteger(1, messageDigest);
        // prepend a zero to get a "proper" MD5 hash value
        StringBuffer sb = new StringBuffer('0');
        sb.append(number.toString(16));
        return sb.toString();
    }

}
