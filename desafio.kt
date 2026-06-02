// Desafio: Aprenda Kotlin Com Exemplos - Lab
// Domínio: Formações, Conteúdos Educacionais e Alunos

import java.util.UUID

// 1. Classe base para Conteúdo Educacional
open class ConteudoEducacional(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val cargaHoraria: Int
) {
    override fun toString(): String = "$titulo (${cargaHoraria}h)"
}

// 2. Subtipos de Conteúdo (opcional, mas enriquece o domínio)
class Curso(titulo: String, cargaHoraria: Int, val tecnologia: String) : ConteudoEducacional(titulo = titulo, cargaHoraria = cargaHoraria)

class Desafio(titulo: String, cargaHoraria: Int, val nivelDificuldade: String) : ConteudoEducacional(titulo = titulo, cargaHoraria = cargaHoraria)

// 3. Classe Aluno
data class Aluno(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val email: String
) {
    override fun toString(): String = "$nome ($email)"
}

// 4. Exceção customizada (usando Nothing)
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}

// 5. Classe Formacao (core do desafio)
class Formacao(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val nivel: String, // Ao invés dos enum
    private val conteudosEducacionais: MutableList<ConteudoEducacional> = mutableListOf()
) {
    // Lista de alunos matriculados
    private val alunosMatriculados: MutableList<Aluno> = mutableListOf()
    
    // Propriedade calculada para carga horária total
    val cargaHorariaTotal: Int
        get() = conteudosEducacionais.sumOf { it.cargaHoraria }
    
    // Adicionar conteúdo educacional 
    fun adicionarConteudo(conteudo: ConteudoEducacional): Formacao {
        conteudosEducacionais.add(conteudo)
        return this
    }
    
    // Adicionar múltiplos conteúdos de uma vez
    fun adicionarConteudos(vararg conteudos: ConteudoEducacional): Formacao {
        conteudos.forEach { adicionarConteudo(it) }
        return this
    }
    
    // Matricular um aluno
    fun matricular(aluno: Aluno): Formacao {
        // Elvis com expressão throw
        val nomeValidado = aluno.nome.takeIf { it.isNotBlank() } ?: fail("Nome do aluno é obrigatório")
        val emailValidado = aluno.email.takeIf { it.isNotBlank() && it.contains("@") } ?: fail("Email inválido")
        
        if (alunosMatriculados.any { it.id == aluno.id }) {
            println(" Aluno ${aluno.nome} já está matriculado nesta formação")
            return this
        }
        
        alunosMatriculados.add(aluno.copy(nome = nomeValidado, email = emailValidado))
        println(" Aluno ${aluno.nome} matriculado com sucesso em '$nome'")
        return this
    }
    
    // Matricula múltiplos alunos 
    fun matricularAlunos(vararg alunos: Aluno): Formacao {
        alunos.forEach { matricular(it) }
        return this
    }
    
    // Lista alunos matriculados 
    fun listarAlunos(): List<Aluno> {
        return alunosMatriculados.let { lista ->
            if (lista.isEmpty()) {
                println(" Nenhum aluno matriculado em '$nome' ainda.")
                emptyList()
            } else {
                println(" Alunos matriculados em '$nome':")
                lista.forEachIndexed { index, aluno ->
                    println("   ${index + 1}. $aluno")
                }
                lista
            }
        }
    }
    
    // Lista conteúdos da formação
    fun listarConteudos() {
        println("\n Conteúdos da formação '$nome' (Nível: $nivel):")
        println("   Carga horária total: ${cargaHorariaTotal}h")
        conteudosEducacionais.forEachIndexed { index, conteudo ->
            println("   ${index + 1}. $conteudo")
        }
    }
    
    override fun toString(): String {
        return "Formacao: $nome ($nivel) - ${conteudosEducacionais.size} conteúdos, ${alunosMatriculados.size} alunos matriculados"
    }
}


fun main() {
    println("🎓 === DESAFIO KOTLIN - FORMAÇÕES DIO === \n")
    
    // Criando conteúdos educacionais
    val conteudo1 = Curso("Introdução ao Kotlin", 40, "Kotlin")
    val conteudo2 = Curso("POO com Kotlin", 60, "Kotlin")
    val conteudo3 = Desafio("Desafio: API REST", 20, "Avançado")
    val conteudo4 = Curso("Android com Kotlin", 80, "Android")
    val conteudo5 = Desafio("Projeto Final", 30, "Médio")
    
    // Criando uma formação usando apply (configuração fluente)
    val formacaoAndroid = Formacao(
        nome = "Android Developer",
        nivel = "Avançado"
    ).apply {
        adicionarConteudos(conteudo1, conteudo2, conteudo4, conteudo5)
    }
    
    // Criando outra formação usando o builder pattern com also
    val formacaoKotlinBasico = Formacao(
        nome = "Kotlin Fundamentals",
        nivel = "Básico"
    ).also { formacao ->
        formacao.adicionarConteudo(conteudo1)
        formacao.adicionarConteudo(conteudo2)
    }
    
    // Criando alunos
    val aluno1 = Aluno(nome = "Bruno Lazarin", email = "blaza@email.com")
    val aluno2 = Aluno(nome = "Fabricia Souza", email = "fabisouza@email.com")
    val aluno3 = Aluno(nome = "Milany Mendes", email = "Mila@email.com")
    
    // Matriculando alunos 
    formacaoAndroid
        .matricularAlunos(aluno1, aluno2)
        .matricular(aluno3)
        .listarAlunos()
    
    formacaoAndroid.listarConteudos()
    
    println("\n" + "=".repeat(50) + "\n")
    
    formacaoKotlinBasico
        .matricular(aluno1)
        .listarAlunos()
    
    formacaoKotlinBasico.listarConteudos()
    
    // Testando validação com throw (exceção)
    println("\n Testando validação:")
    try {
        val alunoInvalido = Aluno(nome = "", email = "emailerrado")
        formacaoAndroid.matricular(alunoInvalido)
    } catch (e: IllegalArgumentException) {
        println(" Exceção capturada: ${e.message}")
    }
    
    // Demonstração do tipo Nothing? com null
    val valorNulo: Nothing? = null
    println("\n Tipo Nothing? demonstrado: $valorNulo")
    
    // Demonstração de try como expressão
    val resultadoConversao = try {
        "42".toInt()
    } catch (e: NumberFormatException) {
        -1
    }
    println("Try como expressão: $resultadoConversao")
    
    println("\n === FIM DO DESAFIO ===")

    //Considerações finais:
    // - O código foi estruturado para ser claro e didático, utilizando os conceitos de Kotlin de forma prática.
    // - As formações e conteúdos foram criados para exemplificar o domínio de forma realista, mas sem complicar demais.
    // - A validação de alunos e o uso de exceções demonstram como lidar com erros de forma elegante em Kotlin.
    // - O uso de propriedades calculadas e funções de extensão poderia ser explorado mais a fundo, mas o foco foi manter o código acessível para iniciantes.
}