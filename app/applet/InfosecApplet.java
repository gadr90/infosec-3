package applet;

import checker.DigitalSignatureChecker;
import checker.EnvelopeChecker;
import checker.PrivateKeyChecker;

import javax.crypto.*;
import java.applet.Applet;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

public class InfosecApplet extends Applet {
    public static final String version = "1.0.0";
    public static byte[] sign(String password, byte[] privateKeyBytes, byte[] randomBytes) {
        System.out.println("Version " + version);
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        System.out.println("Password: " + password);
        System.out.println("File: " + new String(privateKeyBytes));
        System.out.println("Random: " + new String(randomBytes));
        PrivateKey privateKey;
        String signature = "";
        try {
            privateKey = privateKeyChecker.decryptPrivateKey(privateKeyBytes, password);
            System.out.println("PrivateKey: " + privateKey.toString());
            return privateKeyChecker.sign(privateKey, randomBytes);
        } catch (Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
        return new byte[]{0};
    }

    public static byte[] decryptPrivateKey(byte[] PKCS5EncodedPrivateKey, String password) {
        System.out.println("Version " + version);
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        SecureRandom random = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes("UTF-8"));
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56, random);

            byte[] privateKey = privateKeyChecker.decryptPKCS5(PKCS5EncodedPrivateKey, keyGen.generateKey());
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!");
        }
        return new byte[]{0};
    }

    public static String getIndex(byte[] privateKeyContent, byte[] publicKeyContent,
                                  byte[] envelopeContent, byte[] signatureContent, byte[] encryptedContent) {
        System.out.println("Version " + version);
        try {
            DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
            PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();

            PublicKey publicKey = digitalSignatureChecker.readPublicKey(publicKeyContent);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);
            Key key = EnvelopeChecker.getKeyFromSeed(seed);
            byte[] content = privateKeyChecker.decryptPKCS5(encryptedContent, key);

            boolean result = digitalSignatureChecker.verifySignature(publicKey, signatureContent, content);
            return result? new String(content, "UTF-8") : "NOT OK";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!");
        }
        return "Error!";
    }

    public static String checkStatus(byte[] privateKeyContent, byte[] publicKeyContent,
                                  byte[] envelopeContent, byte[] signatureContent, byte[] encryptedContent) {

        try {
            DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
            PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();

            PublicKey publicKey = digitalSignatureChecker.readPublicKey(publicKeyContent);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);
            Key key = EnvelopeChecker.getKeyFromSeed(seed);
            byte[] content = privateKeyChecker.decryptPKCS5(encryptedContent, key);

            boolean result = digitalSignatureChecker.verifySignature(publicKey, signatureContent, content);
            return result? "OK" : "NOT OK";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!");
        }
        return "NOT OK";
    }

    public static String getBase64File(byte[] privateKeyContent, byte[] publicKeyContent,
                                     byte[] envelopeContent, byte[] signatureContent, byte[] encryptedContent) {

        try {
            DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
            PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();

            PublicKey publicKey = digitalSignatureChecker.readPublicKey(publicKeyContent);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);
            Key key = EnvelopeChecker.getKeyFromSeed(seed);
            byte[] content = privateKeyChecker.decryptPKCS5(encryptedContent, key);

            boolean result = digitalSignatureChecker.verifySignature(publicKey, signatureContent, content);

            if (result) {
                byte[] base64Content = Base64.encodeBase64(content);
                return new String(base64Content, "UTF-8");
            } else
                return "Erro";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error!");
        }
        return "Opa";
    }
}
