/*
 * Copyright (C) 2011 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This file is part of AdAway.
 * 
 * AdAway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AdAway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AdAway.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.adaway.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class TcpdumpLogLoader extends AsyncTaskLoader<ArrayList<String>> {

    Context context;
    List<String> mItems;

    public TcpdumpLogLoader(Context context) {
        super(context);

        this.context = context;
    }

    @Override
    public ArrayList<String> loadInBackground() {
        // hashset, because every hostname should be contained only once
        HashSet<String> set = new HashSet<String>();

        try {
            String cachePath = context.getCacheDir().getCanonicalPath();
            String filePath = cachePath + Constants.FILE_SEPERATOR + Constants.TCPDUMP_LOG;

            File file = new File(filePath);
            try {
                // open the file for reading
                FileInputStream instream = new FileInputStream(file);

                // if file the available for reading
                if (instream != null) {
                    // prepare the file for reading
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader reader = new BufferedReader(inputreader);

                    // read every line of the file into the line-variable, on line at the time
                    String nextLine;
                    String hostname;
                    while ((nextLine = reader.readLine()) != null) {
                        Log.d(Constants.TAG, "nextLine: " + nextLine);

                        hostname = RegexUtils.getTcpdumpHostname(nextLine);
                        if (hostname != null) {
                            set.add(hostname);
                        }
                    }
                }
                // close the file again
                instream.close();
            } catch (java.io.FileNotFoundException e) {
                Log.e(Constants.TAG, "Tcpdump log is not existing!");
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "Can not get cache dir: " + e);
            e.printStackTrace();
        }

        ArrayList<String> list = null;
        if (set != null) {
            list = new ArrayList<String>(set);

            // Sort the list.
            Collections.sort(list);
        }

        return list;
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void deliverResult(ArrayList<String> data) {
        super.deliverResult(data);
    }

}