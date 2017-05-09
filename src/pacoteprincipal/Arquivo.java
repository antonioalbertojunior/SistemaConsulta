package pacoteprincipal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Antonio Junior
     * edit by Pedro
 */
public class Arquivo {

    private String nomearquivo = "";
    private final ArquivoIndicePrimario indiceprimario = new ArquivoIndicePrimario();
    private ArquivoIndiceSecundario arqIndiceSecundario;
    private String dados = "";
    private int qntcampos = 0;
    private int qntregistros = 0;
    private String[] secFieldNames;

    public void setNameFile(String nome) {
        String dados = "dados/";
        dados = dados.concat(nome);
        dados = dados.concat(".txt");
        this.nomearquivo = dados;
    }

    public void lerArquivo() {
        String dado = "";
        try {
            try (FileReader arq = new FileReader(nomearquivo)) {
                BufferedReader lerArq = new BufferedReader(arq);
                String linha = lerArq.readLine();
                while (linha != null) {
                    dado += linha;
                    linha = lerArq.readLine();
                }
                setQuantidadeRegistros(dado);
                setQuantidadeCampos(dado);
                
                setArquivosSecundario(dado);
                
                setIndicePrimario(dado);
                arq.close();
            }
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo.\n", e.getMessage());
        }
    }

    public void seekFunctionReplaceChar(int valor, String dado) throws IOException {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(nomearquivo, "rw");
            raf.seek(valor);
            raf.writeBytes(dado);
            System.out.println(dado);
            lerArquivo();
            raf.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void seekFunctionReplaceString(int valor, String dado) throws IOException {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(nomearquivo, "rw");
            raf.seek(valor);
            String aux = raf.readLine();
            aux = aux.substring(0, aux.indexOf("#"));
            int tamantigo = aux.length();
            int tamnovo = dado.length();
            if (tamnovo > tamantigo) {
                inserirFinal(dado);
            } else {
                dado = dado.substring(0, dado.length() - 1);
                System.out.println("Sobreescrever Registro");
                while (dado.length() < tamantigo) {
                    dado = dado.concat(" ");
                }
                dado = dado.concat("#");
                System.out.println(dado);
                raf.seek(valor);
                raf.writeBytes(dado);
            }
            lerArquivo();
            raf.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public String seekFunction(int valor) throws IOException {
        RandomAccessFile raf;
        String x = "";
        try {
            raf = new RandomAccessFile(nomearquivo, "r");
            raf.seek(valor);
            String var = raf.readLine();
            x = var.substring(0, var.indexOf('#'));
            raf.close();
            return x;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return x;
    }

    public void consultarRegistro(String param) {

        double start = System.currentTimeMillis();
        int pos = indiceprimario.consultarPosicao(param);
        try {
            System.out.println("Seek para posição: " + pos);
            System.out.println("Registro: " + seekFunction(pos));
            double elapsed = System.currentTimeMillis() - start;
            System.out.println("Tempo de Consulta: " + elapsed + "ms");
            System.out.println("-------------------------------------");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void consultarRegistroSec(String param, int opSecKey) {

        double start = System.currentTimeMillis();
        ArrayList<String> retList = arqIndiceSecundario.findPosit(opSecKey, param);
        int pos;
        for(int i = 0; i < retList.size(); i++){
            pos = this.indiceprimario.consultarPosicao(retList.get(i));
            try {
                System.out.println("Seek para posição: " + pos);
                System.out.println("Registro: " + seekFunction(pos));
                double elapsed = System.currentTimeMillis() - start;
                System.out.println("Tempo de Consulta: " + elapsed + "ms");
                System.out.println("-------------------------------------");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void excluirRegistro(String param) {
        double start = System.currentTimeMillis();
        int pos = this.indiceprimario.consultarPosicao(param);
        try {
            System.out.println("Seek para posição: " + pos);
            String novoregistro = seekFunction(pos);
            novoregistro = novoregistro.substring(1);
            novoregistro = "*".concat(novoregistro);
            seekFunctionReplaceChar(pos, novoregistro);
            double elapsed = System.currentTimeMillis() - start;
            System.out.println("Tempo de Exclusão: " + elapsed + "ms");
            System.out.println("-------------------------------------");
            lerArquivo();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void inserirFinal(String dado) throws IOException {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(nomearquivo, "rw");
            System.out.println(raf.length());
            raf.seek(raf.length());
            raf.writeBytes(dado);
            raf.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void inserirRegistro(String novoregistro) throws IOException {
        double start = System.currentTimeMillis();
        int pos = this.indiceprimario.registroExcluido();
        System.out.println("Posição do registro Excluido: " + pos);
        if (pos == 0) {
            //inserir no final do nomearquivo
            inserirFinal(novoregistro);
        } else {
            seekFunctionReplaceString(pos, novoregistro);
        }
        lerArquivo();
        double elapsed = System.currentTimeMillis() - start;
        System.out.println("Tempo de Inserção " + elapsed + "ms");
        System.out.println("-------------------------------------");
    }

    public void setIndicePrimario(String registros) throws FileNotFoundException {
        HashMap<String, Integer> map = new HashMap<>();
        List list = new ArrayList<>();
        int quantfim = getQuantidadeRegistros();
        int cont = 0;
        int vlor = 0;
        String refSize;
        String chave;
        for (int i = 0; i < quantfim; i++) {
            chave = registros.substring(0, (registros.indexOf("|")));
            if (chave.length() < 15){
                for (int p = chave.length(); p < 15; p++){
                    chave = chave.concat(" ");
                }
            }else if (chave.length() > 15)
                chave = chave.substring(0, 15);
            //chave = chave.substring(0, 4);
            //String nome = registros.substring((registros.indexOf("|") + 1), (registros.indexOf("|") + 1));
            //nome = nome.substring(0, 5);
            //chave = chave.concat(nome);
            
            String reg = registros.substring(0, registros.indexOf('#'));
            if (i == 0) {
                cont = 0;
                vlor = 0;
            }

            registros = registros.substring(reg.length() + 1, registros.length());            
            map.put(chave, vlor);
            list.add(chave);
            vlor += reg.getBytes().length + 1;
            cont = cont + (reg.length() + 1);
        }
        Collections.sort(list);
        int ultimo = list.size();
        list.remove(ultimo - 1);
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Object element = itr.next();
            refSize = map.get(element).toString().trim();
            if (refSize.length() < 5){
                for (int p = refSize.length(); p < 5; p++){
                    refSize = refSize.concat(" ");
                }
            }else if (refSize.length() > 5)
                refSize = refSize.substring(0, 5);
            //this.dados += element.toString() + "|" + map.get(element).toString() + "#" + "\n";
            this.dados += element.toString() + refSize + "\n";
        }

        this.indiceprimario.criarArquivo(dados);
        this.dados = "";
    }

    public void setQuantidadeRegistros(String sub) {
        int cont = 0;
        char cab[] = sub.toCharArray();
        for (int i = 0; i < cab.length; i++) {
            if (cab[i] == '#') {
                cont++;
            }
        }
        this.qntregistros = cont;
    }

    public void setQuantidadeCampos(String sub) {
        int cont = 0;
        char cab[] = sub.toCharArray();
        for (int i = 0; i < cab.length; i++) {
            if (cab[i] == '|') {
                cont++;
            }
        }
        this.qntcampos = cont / getQuantidadeRegistros();
    }

    public int getQuantidadeRegistros() {
        return this.qntregistros;
    }

    public int getQuantidadeCampos() {
        return this.qntcampos;
    }

    public void getIndicePrimario() throws IOException {
        indiceprimario.imprimirChavePrimaria();
    }

    public String getInput() {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        input = scanner.nextLine();
        return input;
    }

    public String getInputRegistro() {
        String campo;
        Scanner scanner = new Scanner(System.in);
        String novoregistro = new String();
        for (int i = 0; i < getQuantidadeCampos() + 1; i++) {
            System.out.println("Inserir campo " + i);
            campo = scanner.nextLine();

            if (i == getQuantidadeCampos()) {
                novoregistro = novoregistro.concat(campo + "#");
            } else {
                novoregistro = novoregistro.concat(campo + "|");
            }
        }
        return novoregistro;
    }
    
    public void setNomesSecKey(String arq){
        String nome;
        int ref, ref1;
        int cont = 0;
        ref  = (arq.indexOf("|") + 1);
        ref1 = arq.indexOf("#");
        secFieldNames = new String[getQuantidadeCampos()];
        
        while((ref < ref1) && (cont < (getQuantidadeCampos()))){
            if(cont == (getQuantidadeCampos()-1)){
                nome = arq.substring(ref, ref1);
                secFieldNames[cont] = nome;
                cont++;
                ref = (arq.indexOf("|", ref) + 1);
            }else{
                nome = arq.substring(ref, arq.indexOf("|", ref));
                secFieldNames[cont] = nome;
                cont++;
                ref = (arq.indexOf("|", ref) + 1);
            } 
        }
    }
    
    public void setArquivosSecundario(String registros){
        setNomesSecKey(registros);
        arqIndiceSecundario = new ArquivoIndiceSecundario(this.getQuantidadeCampos(), getQuantidadeRegistros()-1, registros, secFieldNames);
        arqIndiceSecundario.criarArquivosListas();
    }

    public String[] getSecFieldNames() {
        return secFieldNames;
    }  
    
    public void printIndiceSecundario() throws IOException{
        arqIndiceSecundario.imprimirIndices();
    }
}
