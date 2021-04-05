package br.com.leonardo.olxprojeto.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.leonardo.olxprojeto.R;
import br.com.leonardo.olxprojeto.helper.ConfiguracaoFirebase;
import br.com.leonardo.olxprojeto.helper.Permissoes;
import br.com.leonardo.olxprojeto.model.Anuncio;
import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private ImageView imageUm, imageDois, imageTres;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private Spinner campoEstado, campoCategoria;
    private Anuncio anuncio;
    private StorageReference storage;
    private android.app.AlertDialog dialog;

    private String[] permissoes = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        //configurações inicias
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //Validar s permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);


        inicializarComponentes();
        carregardadosDoSpinner();
    }

     public void salvarAnuncio( ){

         dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

         //Salvar as imagens no Storage
         for (int i = 0; i < listaFotosRecuperadas.size(); i++){
              String urlImagem = listaFotosRecuperadas.get(i);
              int tamanhoLista = listaFotosRecuperadas.size();
              salvarFotoStorage(urlImagem, tamanhoLista, i);
         }
     }

    //Novo método - Resolvendo problemas das imagens dos anuncios
    private void salvarFotoStorage(final String urlmagem, final int totalFotos, int contador) {

        //Cria o nó dentro do storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncio")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlmagem));
        uploadTask.continueWithTask(task -> {
            if(!task.isSuccessful()){
                throw task.getException();
            }
            return imagemAnuncio.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri downloadUrl = task.getResult();
                listaUrlFotos.add(downloadUrl.toString());
                if(totalFotos == listaUrlFotos.size()){
                    anuncio.setFotos(listaUrlFotos);
                    anuncio.salvar();

                    dialog.dismiss();
                    finish();
                    //alerta("Anuncio salvo com sucesso !");
                }
            }
        });
    }

//     public void salvarFotoStorage(String urlString, int totalFotos, int contador){
//         //criar nó no storage
//         StorageReference  imagemAnuncio = storage.child("imagens")
//                 .child("anuncios")
//                 .child(anuncio.getIdAnuncio())
//                 .child("imagem" + contador);
//
//         //Fazer upload da imagem
//         UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
//         uploadTask.addOnSuccessListener(taskSnapshot -> {
//          Task<Uri> firebaseUrl = taskSnapshot.getStorage().getDownloadUrl();
//          String urlConvertida =  firebaseUrl.toString();
//          listaUrlFotos.add(urlConvertida);
//          if (totalFotos == listaUrlFotos.size()){
//              anuncio.setFotos(listaUrlFotos);
//              anuncio.salvar();
//              dialog.dismiss();
//              finish();
//          }
//
//         }).addOnFailureListener(new OnFailureListener() {
//             @Override
//             public void onFailure(@NonNull Exception e) {
//                  exibirMenssagemErro("Falha ao fazer upload da imagem");
//             }
//         });
//     }
     
     public Anuncio configurarAnuncio( ){

        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();       
        String valor = campoValor.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setTelefone(telefone);
        anuncio.setValor(valor);
        anuncio.setDescricao(descricao);

        return anuncio;
     }


    public void validarDadosDoAnuncio(View view){

       anuncio = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());


        if (listaFotosRecuperadas.size() != 0){
            if (!anuncio.getEstado().isEmpty()){
               if (!anuncio.getCategoria().isEmpty()){
                   if (!anuncio.getTitulo().isEmpty()) {
                       if (!valor.isEmpty() && !valor.equals("0")) {
                           if (!anuncio.getTelefone().isEmpty()) {
                              if (!anuncio.getDescricao().isEmpty()) {
                                 salvarAnuncio();

                              }else {
                                   exibirMenssagemErro("Preencha o campo descrição");
                              }

                           }else {
                                 exibirMenssagemErro("Preencha o campo telefone válido");
                           }

                    }else{
                         exibirMenssagemErro("Preencha o campo valor");
                       }

                     }else {
                          exibirMenssagemErro("Preencha o campo título");
                     }
               }else {
                  exibirMenssagemErro("Preencha o campo categoria");
               }

            }else {
                exibirMenssagemErro("Preencha o campo estado");
            }

        }else {
           // Toast.makeText(this, "Selecione ao menos uma foto", Toast.LENGTH_SHORT).show();
            exibirMenssagemErro("Selecione ao menos uma foto");
        }

    }

    public void exibirMenssagemErro(String mensagem){
        Toast.makeText(this,mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.imageCadastroUm :
                escolherImagem(1);
                break;
            case  R.id.imageCadastroDois :
                escolherImagem(2);
                break;
            case  R.id.imageCadastroTres:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            //REcuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configurar o imagaView
            if (requestCode == 1){
                imageUm.setImageURI(imagemSelecionada);
            }else if (requestCode == 2){
                imageDois.setImageURI(imagemSelecionada);
            }else if (requestCode == 3){
                imageTres.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhoImagem);

        }
    }

    private void carregardadosDoSpinner() {

//        String[] estados = new String[]{
//                "SP", "RJ", "MG", "PR"
//        }

        //Configura o spinner de estados
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapter);

        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);
    }


    public void inicializarComponentes(){

        campoDescricao = findViewById(R.id.editDescricao);
        campoTitulo = findViewById(R.id.editTitulo);
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);

        campoEstado =findViewById(R.id.spinnerEstado);
        campoCategoria =findViewById(R.id.spinnerCategoria);

        imageUm = findViewById(R.id.imageCadastroUm);
        imageDois = findViewById(R.id.imageCadastroDois);
        imageTres = findViewById(R.id.imageCadastroTres);
        imageUm.setOnClickListener(this);
        imageDois.setOnClickListener(this);
        imageTres.setOnClickListener(this);

        //Configurar a localidade para PT-BR
        Locale locale = new Locale("pt","BR");
        campoValor.setLocale(locale);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResulado : grantResults ){
            if (permissaoResulado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }
    }


    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões ");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialog, which) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}