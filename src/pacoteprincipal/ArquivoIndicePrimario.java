package pacoteprincipal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 *
 * @author Antonio Junior
     * edit by Pedro
 */
public class ArquivoIndicePrimario {

    private final String arquivo = "dados/indicePrimario.txt";

    ArrayList<Integer> vetpos = new ArrayList<>();

    public void criarArquivo(String dados) {
        File file = new File(arquivo);
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
            writer.flush();
            writer.print(dados);
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int registroExcluido() {
        int seekvalue = 0;
        try {
            try (FileReader arq = new FileReader(arquivo)) {
                BufferedReader lerArq = new BufferedReader(arq);
                String linha = lerArq.readLine();
                while (linha != null) {
                    if (linha.contains("*")) {
//                        String sub = linha.substring(0, linha.indexOf('#'));
//                        sub = sub.substring(sub.indexOf('|') + 1);
                        String sub = linha.substring(0, linha.length());
                        sub = sub.substring(10);
                        seekvalue = Integer.valueOf(sub);
                    }
                    linha = lerArq.readLine();
                }
            }
            return seekvalue;
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo.\n",
                    e.getMessage());
        }
        return seekvalue;
    }

    public int consultarPosicao(String chave) {

        int tamlinha = 0;
        int seekvalue = 0;
        try {
            try (FileReader arq = new FileReader(arquivo)) {
                BufferedReader lerArq = new BufferedReader(arq);
                String linha = lerArq.readLine();
                vetpos.add(0);
                while (linha != null) {
                    vetpos.add(tamlinha += linha.length() + 1);
                    linha = lerArq.readLine();
                }
                seekvalue = buscaBinaria(vetpos, chave, vetpos.size());
                arq.close();
            }

            setVetor();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo.\n",
                    e.getMessage());
        }
        return seekvalue;
    }

    public int buscaBinaria(ArrayList vet, String chave, int Tam) throws IOException {
        int seeks = 0;
        int sup = 0;
        sup = Tam - 1;
        int inf = 0;     //Limite inferior  (o primeiro elemento do vetor em C é zero          )
        int meio, valor;
        String var, chavetemp;
        while (inf <= sup) {
            seeks++;
            meio = (inf + sup) / 2;
            var = randomSeek(vetpos.get(meio));
            //String chavetemp = var.substring(0, var.indexOf("|"));
            if(var != null){
                chavetemp = var.substring(0, 15).trim();
                chave = chave.trim();
                valor = chave.compareTo(chavetemp);
                
                if (valor == 0) {
                //var = var.substring(var.indexOf("|") + 1, var.indexOf("#"));
                var = var.substring(15, var.length()).trim();
                System.out.println(var);
                meio = Integer.valueOf(var);
                System.out.println("\nBusca binária fez: " + seeks + " seeks");
                return meio;
                }
                if (valor < 0) {
                    sup = meio - 1;
                } else {
                    inf = meio + 1;
                }
            }
            else
                return -1;
        }
        return -1;   // não encontrado
    }

    public String randomSeek(int value) throws IOException {
        RandomAccessFile raf;
        String var = "";
        try {
            raf = new RandomAccessFile(arquivo, "r");
            raf.seek(value);
            var = raf.readLine();
            return var;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return var;
    }

    public void setVetor() {
        this.vetpos.clear();
    }

    public void imprimirChavePrimaria() throws IOException {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(arquivo, "r");
            System.out.println("|Chave Primária|Posição|");
            String var = raf.readLine();
            String pos = var;
            while (var != null) {
                pos = var.substring(15, 20);
                var = var.substring(0, 15);
                if(var.charAt(0) != '*')
                    System.out.println("|" + var + "|"+pos+"|");
                var = raf.readLine();
            }
            System.out.println();
            raf.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
