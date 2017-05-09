package pacoteprincipal;

import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author Pedro
 */
public class ArquivoIndiceSecundario {
    private IndiceSecundario[] indicesSecundario;
    private int numbSecKey, numbReg;
    
    public ArquivoIndiceSecundario(int numSecKey,int numReg , String registros, String[] fieldNames){
        indicesSecundario = new IndiceSecundario[numSecKey];
        numbSecKey = numSecKey;
        numbReg    = numReg;
        /*System.out.println("NumReg: "+numbReg);
        System.out.println("NumSecKey: "+numbSecKey);*/
        
        for(int i = 0; i < fieldNames.length; i++){
            indicesSecundario[i] = new IndiceSecundario(fieldNames[i]);
            //System.out.println(fieldNames[i]);
        }
        
        int ref, refEnd, cont, cont1;
        ref = refEnd = cont = 0;
        refEnd = registros.lastIndexOf("#");
        ref  = (registros.indexOf("#") + 1);
        
        /*System.out.println(registros);
        System.out.println(ref);
        System.out.println(refEnd);*/
        String valor = registros.substring(ref, registros.indexOf("|", ref));
        if (valor.length() < 15){
            for (int p = valor.length(); p < 15; p++){
                valor = valor.concat(" ");
            }
        }else if (valor.length() > 15)
            valor = valor.substring(0, 15);
        ref = (registros.indexOf("|", ref) + 1);
        String reg; 
        
        while((ref < refEnd)){
            if(cont >= numSecKey){
                cont = 0;
                valor = registros.substring(ref, registros.indexOf("|", ref));
                if (valor.length() < 15){
                    for (int p = valor.length(); p < 15; p++){
                        valor = valor.concat(" ");
                    }
                }else if (valor.length() > 15)
                    valor = valor.substring(0, 15);
                ref = (registros.indexOf("|", ref) + 1);
            }
            
            if(cont == (numSecKey-1)){
                reg = registros.substring(ref, registros.indexOf("#", ref));
                if (reg.length() < 25){
                    for (int p = reg.length(); p < 25; p++){
                        reg = reg.concat(" ");
                    }
                }else if (reg.length() > 25)
                    reg = reg.substring(0, 25);
                ref = (registros.indexOf("#", ref) + 1);
            }else{
                reg = registros.substring(ref, registros.indexOf("|", ref));
                if (reg.length() < 25){
                    for (int p = reg.length(); p < 25; p++){
                        reg = reg.concat(" ");
                    }
                }else if (reg.length() > 25)
                    reg = reg.substring(0, 25);
                ref = (registros.indexOf("|", ref) + 1);
            }
            
            indicesSecundario[cont].addFieldMap(reg, valor);
            
            //System.out.println(reg+" - "+valor+" - "+cont);

            cont++;
        }      
    }
    
    public void criarArquivosListas(){
        for(int i = 0; i < numbSecKey; i++){
            indicesSecundario[i].criarIndiceListaInvert();
        }
    }
    
    public ArrayList<String> findPosit(int secKeyOp, String secKey){
        return indicesSecundario[secKeyOp].findPosit(secKey);
    }
    
    public void imprimirIndices() throws IOException{
        for(int i = 0; i < numbSecKey; i++){
            indicesSecundario[i].imprimirChaveSecundaria();
        }
    }
}
