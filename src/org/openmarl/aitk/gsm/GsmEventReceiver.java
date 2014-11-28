package org.openmarl.aitk.gsm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class GsmEventReceiver extends BroadcastReceiver {

    public static final String ACTION_SILENT_SMS_RECEIVED =
            "org.openmarl.aitk.gsm.SilentSmsReceived";
    public static final String ACTION_SILENT_SMS_SENT_STATUS =
            "org.openmarl.aitk.gsm.SilentSmsSent";
    public static final String ACTION_SILENT_SMS_DELIVERY_STATUS =
            "org.openmarl.aitk.gsm.SilentSmsDelivery";

    public static final String EXTRA_SMSC = "org.openmarl.aitk.gsm.SMSC";
    public static final String EXTRA_ORIGIN = "org.openmarl.aitk.gsm.ORIGIN";
    public static final String EXTRA_TP_PID = "org.openmarl.aitk.gsm.TP_PID";
    public static final String EXTRA_PDU = "org.openmarl.aitk.gsm.PDU";

    public GsmEventReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_SILENT_SMS_RECEIVED.equals(intent.getAction()))
            handleSilentSmsReceived(context, intent);
        else if (ACTION_SILENT_SMS_SENT_STATUS.equals(intent.getAction()))
            handleSilentSmsSentStatus(context, intent);
        else if (ACTION_SILENT_SMS_DELIVERY_STATUS.equals(intent.getAction()))
            handleSilentSmsDeliveryStatus(context, intent);
        else
            Log.w(TAG, String.format("Unexpected event: %s", intent.getAction()));
    }


    private void handleSilentSmsReceived(Context context, Intent intent) {
        String smsc = intent.getStringExtra(EXTRA_SMSC);
        String origin = intent.getStringExtra(EXTRA_ORIGIN);
        int tppid = intent.getIntExtra(EXTRA_TP_PID, -1);
        byte[] pdu = intent.getByteArrayExtra(EXTRA_PDU);

        String warningMessage = String.format(
                "SMS-Type 0 (TP-PID: 0x%x) from SMSC %s (origin: %s)",
                tppid,
                smsc,
                origin);

        Log.w(TAG, warningMessage);
        Toast.makeText(context, warningMessage, Toast.LENGTH_LONG).show();
    }

    private void handleSilentSmsSentStatus(Context context, Intent intent) {
        Log.d(TAG, "Silent SMS sent to network ...");
    }

    private void handleSilentSmsDeliveryStatus(Context context, Intent intent) {
        byte[] pdu = intent.getByteArrayExtra("pdu");
        if (pdu != null) {
            String statusReport = String.format("SMS-STATUS-REPORT: %s",
                    GsmUtils.bytesToHexString(pdu));

            SmsMessage sms = SmsMessage.createFromPdu(pdu);
            int tp_st = sms.getStatus();
            if (tp_st == 0)
                Log.i(TAG, "Silent SMS successfully delivered");
            else
                Log.w(TAG, "Silent SMS delivery failed or pending");

            Log.d(TAG, statusReport);
        }
    }

    private static final String TAG = GsmEventReceiver.class.getSimpleName();
}
