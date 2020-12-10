package edu.br.unifafibe.tarefa4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView notesListView;

    ArrayList<String> listaWebsite = new ArrayList<>();

    ArrayList<String> listaLatitudes = new ArrayList<>();

    ArrayList<String> listaLongitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //******* DEFINE A LISTA
        notesListView = (ListView) findViewById(R.id.lvLista);

        //****** CRIA A REQUISIÇÃO HTTP QUE RETONA UM JSONARRAY
        String url = "https://jsonplaceholder.typicode.com/users";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++){
                                listaWebsite.add(response.getJSONObject(i).getString("website").toString());

                                listaLatitudes.add(response.getJSONObject(i).getJSONObject("address").getJSONObject("geo").getString("lat").toString());

                                listaLongitudes.add(response.getJSONObject(i).getJSONObject("address").getJSONObject("geo").getString("lng").toString());

                            }

                            //****** MOSTRA A LISTA NA TELA
                            notesListView.setAdapter(new ArrayAdapter<String>(
                                    getApplicationContext(),
                                    android.R.layout.simple_list_item_activated_1,
                                    android.R.id.text1,
                                    listaWebsite
                            ));

                        }
                        catch (Exception ex){
                            System.out.println("Não foi possível retornar as cidades");
                            System.out.println(ex);
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                System.err.println(error);
            }
        });

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);

        //******** CHAMA A TELA DE MAPA QUANDO O USUÁRIO CLICA
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent it = new Intent(MainActivity.this, MapsActivity.class);

                it.putExtra("Website", listaWebsite.get(position));

                it.putExtra("Lat", Double.parseDouble(listaLatitudes.get(position)));

                it.putExtra("Long", Double.parseDouble(listaLongitudes.get(position)));

                startActivity(it);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Bundle b = getIntent().getExtras();

        String nome = (String) b.get("Website");

        if(b != null){

            SharedPreferences sharedPreferences = getSharedPreferences("P1", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Website", nome);

            editor.apply();
        }


    }
}
