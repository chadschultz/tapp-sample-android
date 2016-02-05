package com.simplytapp.sample;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simplytapp.sample.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Accept two lat/long pairs from the user. Ping the weather service for weather data on both
 * points, calculate how long each location has between sunrise and sunset, and display the
 * difference in sunlight time between those points.
 *
 * TODO: Potential improvements:
 * Validating fields on focus changes
 * Unit testing, at least for date calculations
 * Explicitly check WeatherResponse object to ensure all needed data is present
 * Make more tablet friendly, perhaps with a maximum-width layout that can float in the screen.
 * Field validation to ensure contents are in proper format
 *
 */
public class SampleActivity extends AppCompatActivity {
    public static final String API_BASE_URL = "http://api.openweathermap.org/";
    public static final int STATUS_CODE_SUCCESS = 200;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int SECONDS_IN_HOUR = 60 * 60;

    private TextInputLayout mFirstLatitudeTextInputLayout;
    private EditText mFirstLatitudeEditText;
    private TextInputLayout mFirstLongitudeTextInputLayout;
    private EditText mFirstLongitudeEditText;
    private TextInputLayout mSecondLatitudeTextInputLayout;
    private EditText mSecondLatitudeEditText;
    private TextInputLayout mSecondLongitudeTextInputLayout;
    private EditText mSecondLongitudeEditText;
    private TextView mWeatherReportTextView;
    private ProgressBar mProgressBar;

    private WeatherService mWeatherService;
    private WeatherResponse mFirstWeatherResponse;
    private WeatherResponse mSecondWeatherResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mWeatherService = retrofit.create(WeatherService.class);

        mFirstLatitudeTextInputLayout = (TextInputLayout) findViewById(R.id.first_latitude_text_input);
        mFirstLatitudeEditText = (EditText) findViewById(R.id.first_latitude_edit_text);
        mFirstLongitudeTextInputLayout = (TextInputLayout) findViewById(R.id.first_longitude_text_input);
        mFirstLongitudeEditText = (EditText) findViewById(R.id.first_longitude_edit_text);
        mSecondLatitudeTextInputLayout = (TextInputLayout) findViewById(R.id.second_latitude_text_input);
        mSecondLatitudeEditText = (EditText) findViewById(R.id.second_latitude_edit_text);
        mSecondLongitudeTextInputLayout = (TextInputLayout) findViewById(R.id.second_longitude_text_input);
        mSecondLongitudeEditText = (EditText) findViewById(R.id.second_longitude_edit_text);
        mSecondLongitudeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit();
                    return true;
                }
                return false;
            }
        });
        Button checkWeatherButton = (Button) findViewById(R.id.check_weather_button);
        checkWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        mWeatherReportTextView = (TextView) findViewById(R.id.weather_report_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    /**
     * User wants to get the weather report from the server. Submit parameters and wait for
     * both results.
     */
    private void submit() {
        if (!isInputValid()) {
            return;
        }

        // Replace previous weather report (if any) with a loading indicator
        mWeatherReportTextView.setText("");
        mProgressBar.setVisibility(View.VISIBLE);
        // Reset--when null, we know these haven't been loaded yet
        mFirstWeatherResponse = null;
        mSecondWeatherResponse = null;

        serverCall(mFirstLatitudeEditText.getText().toString(),
                mFirstLongitudeEditText.getText().toString(), true);
        serverCall(mSecondLatitudeEditText.getText().toString(),
                mSecondLongitudeEditText.getText().toString(), false);
    }

    public void serverCall(String latString, String longString, final boolean firstResponse) {
        Call<WeatherResponse> weatherResponseCall =
                mWeatherService.weatherResponse(latString, longString, getString(R.string.api_key));
        weatherResponseCall.enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(Response<WeatherResponse> response) {
                mProgressBar.setVisibility(View.GONE);

                int statusCode = response.code();
                if (statusCode != STATUS_CODE_SUCCESS) {
                    // HTTP request itself fails
                    mWeatherReportTextView.setText(response.message());
                }

                WeatherResponse weatherResponse = response.body();
                if (weatherResponse.getCod() != STATUS_CODE_SUCCESS) {
                    // The weather service doesn't like the data. It gives us a response, with
                    // their own "cod" attribute in addition to the status code of the HTTP request.
                    // This may display an error code when the HTTP status code does not.
                    mWeatherReportTextView.setText(getString(R.string.error_weather_service));
                } else {
                    if (firstResponse) {
                        mFirstWeatherResponse = weatherResponse;
                        if (mSecondWeatherResponse != null) {
                            // Make sure both calls have returned successfully
                            showResults();
                        }
                    } else {
                        mSecondWeatherResponse = weatherResponse;
                        if (mFirstWeatherResponse != null) {
                            // Make sure both calls have returned successfully
                            showResults();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mWeatherReportTextView.setText(t.getMessage());
            }
        });
    }

    /**
     * Calculate amount of sunset and show the report below the button on the screen.
     */
    private void showResults() {
        String firstCityName = mFirstWeatherResponse.getName();
        String secondCityName = mSecondWeatherResponse.getName();

        long firstSunDuration = mFirstWeatherResponse.getSys().getSunset() - mFirstWeatherResponse.getSys().getSunrise();
        long secondSunDuration = mSecondWeatherResponse.getSys().getSunset() - mSecondWeatherResponse.getSys().getSunrise();
        long sunDifference = firstSunDuration - secondSunDuration;
        if (sunDifference == 0) {
            // Both locations have equal amounts of sunlight, down to the second
            mWeatherReportTextView.setText(getString(R.string.weather_report_equal, firstCityName, secondCityName));
        } else {
            // The locations have different amounts of sunlight. Calculate the difference.
            // Note: the weather service uses unix time (seconds) instead of Java time (milliseconds)
            long absDifference = Math.abs(sunDifference); // minus sign not needed in text
            int hours = (int) absDifference / SECONDS_IN_HOUR;
            absDifference %= SECONDS_IN_HOUR; // subtract hours, leaving minutes and seconds
            int minutes = (int) absDifference / SECONDS_IN_MINUTE;
            absDifference %= SECONDS_IN_MINUTE; //subtract minutes, leaving seconds
            int seconds = (int) absDifference;

            if (sunDifference > 0) {
                // First location has more sunlight
                mWeatherReportTextView.setText(getString(R.string.weather_report_more, firstCityName, hours, minutes, seconds, secondCityName));
            } else {
                // First location has less sunlight
                mWeatherReportTextView.setText(getString(R.string.weather_report_less, firstCityName, hours, minutes, seconds, secondCityName));
            }
        }
    }

    private void clearErrors() {
        mFirstLatitudeTextInputLayout.setError(null);
        mFirstLongitudeTextInputLayout.setError(null);
        mSecondLatitudeTextInputLayout.setError(null);
        mSecondLongitudeTextInputLayout.setError(null);
    }

    private boolean isInputValid() {
        clearErrors();

        boolean inputValid = true;

        // First latitude must be present
        if (TextUtils.isEmpty(mFirstLatitudeEditText.getText().toString())) {
            inputValid = false;
            mFirstLatitudeTextInputLayout.setError(getString(R.string.error_missing_latitude));
        }

        // First longitude must be present
        if (TextUtils.isEmpty(mFirstLongitudeEditText.getText().toString())) {
            inputValid = false;
            mFirstLongitudeTextInputLayout.setError(getString(R.string.error_missing_longitude));
        }

        // Second latitude must be present
        if (TextUtils.isEmpty(mSecondLatitudeEditText.getText().toString())) {
            inputValid = false;
            mSecondLatitudeTextInputLayout.setError(getString(R.string.error_missing_latitude));
        }

        // Second longitude must be present
        if (TextUtils.isEmpty(mSecondLongitudeEditText.getText().toString())) {
            inputValid = false;
            mSecondLongitudeTextInputLayout.setError(getString(R.string.error_missing_longitude));
        }

        return inputValid;
    }
}