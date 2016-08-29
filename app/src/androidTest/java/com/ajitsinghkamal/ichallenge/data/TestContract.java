package com.ajitsinghkamal.ichallenge.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by deku on 21/8/16.
 */
public class TestContract extends AndroidTestCase{


        // intentionally includes a slash to make sure Uri is getting quoted correctly
        private static final String TEST_WEATHER_LOCATION = "/testChallenge";
        private static final long TEST_WEATHER_DATE = 1419033600L;  // December 20th, 2014

        /*
            Students: Uncomment this out to test your weather location function.
         */
        public void testBuildWeatherLocation() {
            Uri locationUri = challengeContract.Progress.buildProgressChallengeUri(TEST_WEATHER_LOCATION);
            assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                            "WeatherContract.",
                    locationUri);
            assertEquals("Error: Weather location not properly appended to the end of the Uri",
                    TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
            assertEquals("Error: Weather location Uri doesn't match our expected result",
                    locationUri.toString(),
                    "content://com.ajitsinghkamal/progress/testChallenge");
        }
    }

