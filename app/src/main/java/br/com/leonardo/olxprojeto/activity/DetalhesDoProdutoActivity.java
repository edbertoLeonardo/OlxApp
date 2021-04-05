package br.com.leonardo.olxprojeto.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import br.com.leonardo.olxprojeto.R;
import br.com.leonardo.olxprojeto.model.Anuncio;

public class DetalhesDoProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo,  descricao, estado, preco;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_do_produto);

        //Configurar toolbar
        getSupportActionBar().setTitle("Detalhe do produto");

        //Iniciar os componentes
        inicailizarComponentes();

        //Recupera anuncio
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
         if (anuncioSelecionado != null){

             titulo.setText(anuncioSelecionado.getTitulo());
             descricao.setText(anuncioSelecionado.getDescricao());
             estado.setText(anuncioSelecionado.getEstado());
             preco.setText(anuncioSelecionado.getValor());


             ImageListener imageListener = new ImageListener() {
                 @Override
                 public void setImageForPosition(int position, ImageView imageView) {
                     String urlString = anuncioSelecionado.getFotos().get(position);
                     Picasso.get().load(urlString).into(imageView);
                 }
             };

             carouselView.setPageCount(anuncioSelecionado.getFotos().size());
             carouselView.setImageListener(imageListener);

         }

    }

    public void visualizarTelefone(View view){

        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("Tel", anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }


    private void inicailizarComponentes(){

        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        descricao = findViewById(R.id.textdescricaoDetalhe);
        estado = findViewById(R.id.textEstadoDetalhe);
        preco = findViewById(R.id.textPrecoDetalhe);
    }
}