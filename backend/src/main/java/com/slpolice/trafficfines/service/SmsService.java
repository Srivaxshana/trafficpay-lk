package com.slpolice.trafficfines.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhone;

    public void sendSms(String toPhone, String messageBody) {
        if (accountSid.startsWith("YOUR_") || accountSid.isBlank()) {
            logger.info("[SMS MOCK] To: {} | Message: {}", toPhone, messageBody);
            return;
        }
        try {
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                    new PhoneNumber(toPhone),
                    new PhoneNumber(fromPhone),
                    messageBody
            ).create();
            logger.info("SMS sent successfully. SID: {}", message.getSid());
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", toPhone, e.getMessage());
        }
    }

    public void notifyOfficerPaymentReceived(String officerPhone, String referenceNumber,
                                              String driverName, String vehicleNumber) {
        String msg = String.format(
            "SL Police Traffic Fines: Payment received for fine %s. Driver: %s, Vehicle: %s. Driver's license may be released.",
            referenceNumber, driverName, vehicleNumber
        );
        sendSms(officerPhone, msg);
    }
}
