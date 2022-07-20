package suresh.chandra.retrofitpostrequestapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    EditText etName,etTrip;
    Button btSubmit;
    RecyclerView recyclerView;

    String sBaseUrl = "https://api.instantwebtools.net/v1/";
    String sName,sTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);
        etTrip = findViewById(R.id.et_trip);
        btSubmit = findViewById(R.id.btn_submit);
        recyclerView = findViewById(R.id.recyclerView);
        //Call method
        getPassenger();
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sName = etName.getText().toString().trim();
                sTrip = etTrip.getText().toString().trim();
                //Check condition
                if (!sName.isEmpty()&& !sTrip.isEmpty()){
                    addPassenger();
                }
            }
        });
    }
    //API Interface
    private interface getInter{
        //Get request
        @GET("passemger")
        Call<String> STRING_CALL(
                @Query("page") String page,
                @Query("size") String size
        );
    }
    //API Method
    private void getPassenger(){
        ProgressDialog dialog = ProgressDialog.show(
                this,"","Please Wait...",true
        );
        //Initialize progress dialog
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(sBaseUrl)
              //  .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Initialize interface
        getInter inter = retrofit.create(getInter.class);
        //pass input value
        Call<String> call = inter.STRING_CALL("756","25");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //check condition
                if (response.isSuccessful() && response.body()!=null){
                    dialog.dismiss();
                    try {
                        //Intialize json object
                        JSONObject object = new JSONObject(response.body());
                        //Get json array
                        JSONArray jsonArray = object.getJSONArray("data");
                        //Initialize layout manager
                        GridLayoutManager layoutManager = new GridLayoutManager(
                                MainActivity.this,2
                        );
                        recyclerView.setLayoutManager(layoutManager);
                        //set Adapter
                        recyclerView.setAdapter(new MainAdapter(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    //API interface
    private interface postInter{
        //Post request
        @FormUrlEncoded
        @POST("passenger")
        //Initialize string
        Call<String>STRING_CALL(
                @Field("name") String name,
                @Field("trip") String trip,
                @Field("airline") String airline
        );
    }
    //API Method
    private void addPassenger(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(sBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Initialize interface
        postInter inter = retrofit.create(postInter.class);
        //pass input value
        Call<String> call = inter.STRING_CALL(sName,sTrip,"1");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body()!=null){
                    etName.getText().clear();
                    etTrip.getText().clear();
                    getPassenger();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}