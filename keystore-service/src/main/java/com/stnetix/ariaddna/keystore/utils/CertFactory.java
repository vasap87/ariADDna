package com.stnetix.ariaddna.keystore.utils;

import com.stnetix.ariaddna.commonutils.logger.AriaddnaLogger;
import com.stnetix.ariaddna.keystore.exceptions.KeyStoreException;
import sun.security.x509.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Date;

/**
 * Created by alexkotov on 20.04.17.
 */
public class CertFactory {
    private static final CertFactory CERT_FACTORY = new CertFactory();
    private CertFactory(){}

    public static CertFactory getCertFactory() {
        return CERT_FACTORY;
    }
    private static final AriaddnaLogger LOGGER;
    private static final Date FROM;
    private static final Date TO;
    private static final int DAYS;

    static {
        LOGGER = AriaddnaLogger.getLogger(CertFactory.class);
        FROM = new Date(System.currentTimeMillis());
        DAYS = 365 * 10;
        TO = new Date(System.currentTimeMillis() + 86400000L * DAYS );
    }

    public File getNewCertificate(String alias) throws KeyStoreException{
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            X509CertInfo certInfo = new X509CertInfo();
            CertificateValidity interval = new CertificateValidity(FROM,TO);
            BigInteger sn = new BigInteger(64, new SecureRandom());
            X500Name owner = new X500Name("CN="+alias+", L=Russia, C=RU");

            certInfo.set(X509CertInfo.VALIDITY, interval);
            certInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
            certInfo.set(X509CertInfo.SUBJECT, owner);
            certInfo.set(X509CertInfo.ISSUER, owner);
            certInfo.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
            certInfo.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            AlgorithmId algorithm = new AlgorithmId(AlgorithmId.md2WithRSAEncryption_oid);
            certInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algorithm));

            X509CertImpl cert = new X509CertImpl(certInfo);
            cert.sign(privateKey,"SHA1withRSA");

            algorithm = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
            certInfo.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algorithm);
            cert = new X509CertImpl(certInfo);
            cert.sign(privateKey, "SHA1withRSA");

            File certFile = new File(alias+".cer");
            if(certFile.createNewFile()){
                FileOutputStream fos = new FileOutputStream(certFile);
                fos.write(cert.getEncoded());
                fos.close();
            }
            LOGGER.info("Certificate generated with filename {}", certFile.getAbsolutePath());
            return certFile;

        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    public boolean isValid(File certFile) throws KeyStoreException {
        try {
            X509CertImpl cert = (X509CertImpl) getCertByFile(certFile);
            long notBefore = cert.getNotBefore().getTime();
            long notAfter = cert.getNotAfter().getTime();
            long now = System.currentTimeMillis();
            LOGGER.info("Certificate {} is " + (now >= notBefore && now <= notAfter ? "valid": "not valid"), certFile.getAbsolutePath());
            return now >= notBefore && now <= notAfter;
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    Certificate getCertByFile(File certFile) throws KeyStoreException {
        try {
            return new X509CertImpl(new FileInputStream(certFile));
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            throw new KeyStoreException("Caused by: ", e);
        }
    }

    String getCertSubjectName(X509CertImpl cert) {
        return cert.getSubjectDN().toString().split(", ")[0].split("=")[1];

    }
}
