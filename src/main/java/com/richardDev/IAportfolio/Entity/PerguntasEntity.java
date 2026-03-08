package com.richardDev.IAportfolio.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "perguntas", schema = "portfolio")
public class PerguntasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pergunta;

    @Column(columnDefinition = "TEXT")
    private String resposta;

    @Column(columnDefinition = "TEXT")
    private String resposta_english;

    public String getResposta_english() { return resposta_english;}
    public void setResposta_english(String resposta_english) { this.resposta_english = resposta_english; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPergunta() { return pergunta; }
    public void setPergunta(String pergunta) { this.pergunta = pergunta; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
}