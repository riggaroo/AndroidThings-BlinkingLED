package za.co.riggaroo.blinkingled;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class BlinkingActivity extends Activity {

    private static final String TAG = "BlinkActivity";
    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;

    private Handler blinkingLedHandler = new Handler();

    private Gpio ledGpio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinking);

        PeripheralManagerService service = new PeripheralManagerService();
        String gpioPinName = BoardDefaults.getGPIOForLED();
        try {
            ledGpio = service.openGpio(gpioPinName);
            ledGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            blinkingLedHandler.post(blinkingRunnable);
        } catch (IOException e) {
            throw new RuntimeException("Problem connecting to IO Port", e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        blinkingLedHandler.removeCallbacks(blinkingRunnable);

        try {
            ledGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    private Runnable blinkingRunnable = new Runnable() {
        @Override
        public void run() {
            if (ledGpio == null) {
                return;
            }

            try {
                ledGpio.setValue(!ledGpio.getValue());

                blinkingLedHandler.postDelayed(blinkingRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

}
