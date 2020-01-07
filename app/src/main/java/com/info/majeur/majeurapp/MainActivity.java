package com.info.majeur.majeurapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.info.majeur.majeurapp.models.Light;
import com.info.majeur.majeurapp.models.Room;
import com.info.majeur.majeurapp.models.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public List<Room> rooms;
    public Room room;
    public ImageButton imageButtonLight;
    public SeekBar lightlevel;
    public ScrollView scrollView;

    public Spinner spinnerRoom;
    public Response.Listener<JSONObject> response_room;
    public Response.ErrorListener errorListener;
    public String apirooms = "https://app-48651f25-1be8-458e-8356-fa4cafdfdc23.cleverapps.io/api/lights/";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButtonLight = findViewById(R.id.imageButtonLight);
        lightlevel = findViewById(R.id.seekBarLight);
        scrollView = findViewById(R.id.scrollView);
        spinnerRoom = findViewById(R.id.spinnerRoom);



        lightlevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(MainActivity.this.room.getLight().getStatus().equals(Status.ON)) {
                    MainActivity.this.setroomdata(MainActivity.this.apirooms + room.getId() + "/light/level/" + seekBar.getProgress());
                } else {
                    MainActivity.this.set();
                }
            }
        });



        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.this.room = MainActivity.this.rooms.get(i);
                set();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        response_room = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String id = response.getString("id");

                    MainActivity.this.room.setId(Long.parseLong(id));

                    JSONObject light = response.getJSONObject("light");
                    String light_status = light.getString("status");
                    String light_level = light.getString("level");

                    MainActivity.this.room.getLight().setLevel(Integer.parseInt(light_level));
                    MainActivity.this.room.getLight().setId(Long.parseLong(light.getString("id")));
                    if(light_status.equals("ON")) {
                        MainActivity.this.room.getLight().setStatus(Status.ON);
                    } else {
                        MainActivity.this.room.getLight().setStatus(Status.OFF);
                    }


                    MainActivity.this.set();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };

        requestQueue = Volley.newRequestQueue(this);


    }



    private void setspinnerRoom() {
        List<String> ids = new ArrayList<>();
        for (Room room : rooms) {
            ids.add(room.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item, ids);
        spinnerRoom.setAdapter(arrayAdapter);

    }


    public void setroomdata(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, response_room, errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public void set() {
        if (room.getLight().getStatus().equals(Status.ON)) {
            imageButtonLight.setImageResource(R.mipmap.light);
            lightlevel.setProgress(room.getLight().getLevel());
        } else {
            imageButtonLight.setImageResource(R.mipmap.lightoff);
            lightlevel.setProgress(0);
        }


    }

    public void switchlight(View view) {
        if(room.getLight().getStatus().equals(Status.ON)) {
            room.getLight().setStatus(Status.OFF);
        } else {
            room.getLight().setStatus(Status.ON);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apirooms + room.getId() + "/switch-light", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String on = null;
                        try {
                            on = response.getString("status");
                            if(on.equalsIgnoreCase("ON")) {
                                MainActivity.this.room.getLight().setStatus(Status.ON);
                            } else {
                                MainActivity.this.room.getLight().setStatus(Status.OFF);
                            }
                            MainActivity.this.set();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, errorListener);
        requestQueue.add(jsonObjectRequest);
    }
}
