package br.com.leonardo.olxprojeto.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.leonardo.olxprojeto.R;
import br.com.leonardo.olxprojeto.adapter.AdapterAnuncios;
import br.com.leonardo.olxprojeto.helper.ConfiguracaoFirebase;
import br.com.leonardo.olxprojeto.helper.RecyclerItemClickListener;
import br.com.leonardo.olxprojeto.model.Anuncio;
import dmax.dialog.SpotsDialog;


public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //Configurações iniciais
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child( ConfiguracaoFirebase.getIdUsuario() );

        inicializarComponentes();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurar RecyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter( adapterAnuncios );

        //Recupera anúncios para o usuário
        recuperarAnuncios();
        //Adicionar evento de click recyclerView
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        Anuncio anuncioSelecionado = anuncios.get(position);
                        anuncioSelecionado.remover();

                        adapterAnuncios.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );
    }

    private void recuperarAnuncios(){

        dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                anuncios.clear();
                for ( DataSnapshot ds : dataSnapshot.getChildren() ){
                    anuncios.add( ds.getValue(Anuncio.class) );
                }

                Collections.reverse( anuncios );
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void inicializarComponentes(){

        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);

    }

}

//public class MeusAnunciosActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerAnuncios;
//    private List<Anuncio> anuncios = new ArrayList<>();
//    private AdapterAnuncios adapterAnuncios;
//    private DatabaseReference anuncioUsuarioRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_meus_anuncios);
//
//        //Configuraçoes inicias
//        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
//                .child("meus-anuncios")
//                .child(ConfiguracaoFirebase.getIdUsuario());
//
//        inicializarComponenetes();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class)));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        //Configurar recyclerView
//        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
//        recyclerAnuncios.setHasFixedSize(true);
//        adapterAnuncios = new AdapterAnuncios(anuncios, this);
//        recyclerAnuncios.setAdapter(adapterAnuncios);
//
//        //Recuperar os anuncios
//        recuperarAnuncios();
//    }
//
//    private void recuperarAnuncios(){
//
//        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                anuncios.clear();
//                for (DataSnapshot ds : dataSnapshot.getChildren()){
//                        anuncios.add(ds.getValue(Anuncio.class));
//                }
//
//                Collections.reverse(anuncios);
//                adapterAnuncios.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    public void inicializarComponenetes(){
//
//        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
//    }
//
//}

