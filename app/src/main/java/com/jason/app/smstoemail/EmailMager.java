package com.jason.app.smstoemail;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailMager {
    //
    private static final String TAG = "EMailMager";
    private static EmailMager instance = new EmailMager();
    //
    private int mMaxRetry = 50;
    private int mRetryTime = 3000;
    //
    private String mSendEmail = "xxxxx@163.com";
    private String mPassword = "xxxxx";
    private String mSendServer = "smtp.163.com";
    private boolean mDebug=false;
    private String mSport ="465";
    private String mPort="465";

    public static EmailMager getInstance() {
        return instance;
    }

    private EmailMager() {

    }

    class MailTask extends AsyncTask<Void, Void, Boolean> {

        private MimeMessage mimeMessage;

        public MailTask(MimeMessage mimeMessage) {
            this.mimeMessage = mimeMessage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int count = mMaxRetry;
            Boolean isSuccess = false;
            //3秒重发，尝试50次
            while (--count > 0) {
                try {
                    Transport.send(mimeMessage);
                    isSuccess = Boolean.TRUE;
                    break;
                } catch (MessagingException e) {
                    Log.e(TAG, "send error=" + e.toString());
                    isSuccess = Boolean.FALSE;
                }
                try {
                    Thread.sleep(mRetryTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return isSuccess;
        }
    }

    public boolean sendMail(final String title, final String content) {
        MimeMessage mimeMessage = createMessage(title, content);
        if (mimeMessage != null) {
            MailTask mailTask = new MailTask(mimeMessage);
            mailTask.execute();
            return true;
        }
        return false;
    }

    public boolean sendMailWithFile(String title, String content, String filePath) {
        MimeMessage mimeMessage = createMessage(title, content);
        if (mimeMessage != null) {
            appendFile(mimeMessage, filePath);
            MailTask mailTask = new MailTask(mimeMessage);
            mailTask.execute();
            return true;
        }
        return false;
    }

    public boolean sendMailWithMultiFile(String title, String content, List<String> pathList) {
        MimeMessage mimeMessage = createMessage(title, content);
        if (mimeMessage != null) {
            appendMultiFile(mimeMessage, pathList);
            MailTask mailTask = new MailTask(mimeMessage);
            mailTask.execute();
            return true;
        }
        return false;
    }

    private Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mSendEmail, mPassword);
            }
        };
    }

    private MimeMessage createMessage(String title, String content) {
        try {
            Properties properties = System.getProperties();
            if (mDebug) {
                properties.put("mail.debug", "true");
            }
            properties.put("mail.smtp.host", mSendServer);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.port", mPort);
            properties.put("mail.smtp.socketFactory.port", mSport);
            properties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "false");
            //
            Session session = Session.getInstance(properties, getAuthenticator());
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(mSendEmail));
            InternetAddress[] addresses = new InternetAddress[]{new InternetAddress(mSendEmail)};
            mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
            mimeMessage.setSubject(title);
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(content, "text/plain; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            mimeMessage.setContent(multipart);
            mimeMessage.setSentDate(new Date());
            return mimeMessage;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void appendFile(MimeMessage message, String filePath) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(filePath);
            multipart.addBodyPart(filePart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendMultiFile(MimeMessage message, List<String> pathList) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            for (String path : pathList) {
                MimeBodyPart filePart = new MimeBodyPart();
                filePart.attachFile(path);
                multipart.addBodyPart(filePart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //setter getter
    public int getMaxRetry() {
        return mMaxRetry;
    }

    public void setMaxRetry(int mMaxRetry) {
        this.mMaxRetry = mMaxRetry;
    }

    public int getRetryTime() {
        return mRetryTime;
    }

    public void setRetryTime(int mRetryTime) {
        this.mRetryTime = mRetryTime;
    }

    public String getSendEmail() {
        return mSendEmail;
    }

    public void setSendEmail(String mSendEmail) {
        this.mSendEmail = mSendEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getSendServer() {
        return mSendServer;
    }

    public void setSendServer(String mSendServer) {
        this.mSendServer = mSendServer;
    }
}

