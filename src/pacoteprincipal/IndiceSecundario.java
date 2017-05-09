package pacoteprincipal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 *
 * @author Pedro
 */
public class IndiceSecundario {
    private Map<String, LinkedList<String>> secKeyMap = new LinkedHashMap<String, LinkedList<String>>();
    private LinkedList<String> mapa = new LinkedList<String>();
    private String fieldName;
    private String nomeArq = "dados/indiceSecundario_";
    private String nomeLL = "dados/listaLigada_";
    
    public IndiceSecundario(String name){
        fieldName = name;
    }

    public void addFieldMap (String key, String value){
        if (!secKeyMap.containsKey(key)){
            secKeyMap.put(key, new LinkedList<String>());
            mapa.add(key);
        }
        
        secKeyMap.get(key).add(value);
    }
    
    public LinkedList getFieldsValues (String key){
        if (!secKeyMap.containsKey(key))
            return secKeyMap.get(key);
        else
            return null;
    }
    
    public void criarIndiceListaInvert(){
        Collections.sort(mapa);
        String campo, arqSec, invertList, strAux;
        invertList = new String();
        arqSec     = new String();
        Iterator itr = mapa.iterator();
        Iterator invL;
        LinkedList<String> lista;
        File fileSec, fileLL ;
        PrintWriter writer;
        
        while(itr.hasNext()){
            campo = (String) itr.next();
            lista = secKeyMap.get(campo);
            arqSec     += campo + lista.getFirst() + "\n";
            invL = lista.iterator();
            invertList += lista.getFirst();
            if(invL.hasNext())
                invL.next();
            
            while(invL.hasNext()){
                strAux = (String)invL.next();
                invertList += strAux + "\n" + strAux;
            }
            
            invertList += "             -1\n";
        }
        
        nomeArq += fieldName + ".txt";
        nomeLL  += fieldName + ".txt";
        fileSec = new File(nomeArq);
        fileLL  = new File(nomeLL);
        
        try {
            writer = new PrintWriter(fileSec);
            writer.flush();
            writer.print(arqSec);
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        
        try {
            writer = new PrintWriter(fileLL);
            writer.flush();
            writer.print(invertList);
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public ArrayList<String> findPosit(String secKey){
        int tamlinha = 0;
        String seekvalue;
        ArrayList<Integer> vetpos = new ArrayList<>();
        try {
            try (FileReader arq = new FileReader(nomeArq)) {
                BufferedReader lerArq = new BufferedReader(arq);
                String linha = lerArq.readLine();
                vetpos.add(0);
                while (linha != null) {
                    vetpos.add(tamlinha += linha.length() + 1);
                    linha = lerArq.readLine();
                }
                seekvalue = binarySearch(vetpos, secKey, vetpos.size());
                arq.close();
                return this.searchInvertList(seekvalue);
            }
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo.\n",
                    e.getMessage());
        }
        return null;
    }
    
    public String binarySearch(ArrayList vet, String chave, int Tam) throws IOException {
        int seeks = 0;
        int sup = 0;
        sup = Tam - 1;
        int inf = 0;     //Limite inferior  (o primeiro elemento do vetor é zero)
        int meio, valor;
        String var, chavetemp;
        while (inf <= sup) {
            seeks++;
            meio = (inf + sup) / 2;
            var = randomSeek((int)vet.get(meio));
            if(var != null){
                chavetemp = var.substring(0, 25).trim();
                chave = chave.trim();
                valor = chave.compareTo(chavetemp);
                
                if (valor == 0){
                    var = var.substring(25, 40).trim();
                    System.out.println("\nBusca binária da chave secundaria fez: " + seeks + " seeks");
                    return var;
                }
                if (valor < 0){
                    sup = meio - 1;
                }else{
                    inf = meio + 1;
                }
            }
            else
                return null;
        }
        return null;// não encontrado
    }
    
    public String randomSeek(int value) throws IOException {
        RandomAccessFile raf;
        String var = "";
        try {
            raf = new RandomAccessFile(nomeArq, "r");
            raf.seek(value);
            var = raf.readLine();
            return var;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return var;
    }
    
    public ArrayList<String> searchInvertList (String seekValue){
        int valor;
        Integer[] result;
        ArrayList<String> listResult = new ArrayList<String>();
        String var, chavetemp, chaveAux;
        seekValue = seekValue.trim();
        listResult.add(seekValue);
        try{
            FileReader arq = new FileReader(nomeLL);
            BufferedReader lerArq = new BufferedReader(arq);
            String linha = lerArq.readLine();
            while (linha != null) {
                chavetemp = linha.substring(0, 15).trim();
                valor = seekValue.compareTo(chavetemp);

                if (valor == 0) {
                    var = linha.substring(15, 30).trim();
                    if (var.compareTo("-1") == 0){
                        return listResult;
                    }else if(var.charAt(0) != '*'){
                        listResult.add(var);
                        seekValue = var;
                    } 
                }
                linha = lerArq.readLine();
            }
            arq.close();
            return listResult;
        }catch (IOException e) {
        System.err.printf("Erro na abertura do arquivo.\n",
            e.getMessage());
        }
        return listResult;
    }
    
    public void imprimirChaveSecundaria() throws IOException {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(nomeArq, "r");
            System.out.println("|    Chave Secundaria     | Chave InvList |");
            String var = raf.readLine();
            String pos = var;
            while (var != null) {
                pos = var.substring(25, 40);
                var = var.substring(0, 25);
                if(pos.charAt(0) != '*')
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
