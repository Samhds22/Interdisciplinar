package com.ceunsp.app.projeto.Model;

public class Annotation {

    private String tituloAnotacao;
    private String descAnotacao;
    private String usuarioAtivo;
    private String key;


    public Annotation() {
    }

    public Annotation(String tituloAnotacao, String descAnotacao, String usuarioAtivo, String key) {
        this.tituloAnotacao = tituloAnotacao;
        this.descAnotacao = descAnotacao;
        this.usuarioAtivo = usuarioAtivo;
        this.key = key;
   }

    public String getTituloAnotacao() {
        return tituloAnotacao;
    }

    public void setTituloAnotacao(String tituloAnotacao) {
        this.tituloAnotacao = tituloAnotacao;
    }

    public String getDescAnotacao() {
        return descAnotacao;
    }

    public void setDescAnotacao(String descAnotacao) {
        descAnotacao = descAnotacao;
    }

    public String getUsuarioAtivo() {
        return usuarioAtivo;
    }

    public void setUsuarioAtivo(String usuarioAtivo) {
        this.usuarioAtivo = usuarioAtivo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}

