package pacoteprincipal;

import java.io.IOException;
import java.util.Scanner;
import java.io.File;

/**
 *
 * @author Antonio Junior
     * edit by Pedro
 */
public class Principal {


    public static void main(String[] args) throws IOException {
        Arquivo arquivo = new Arquivo();
        Scanner entrada = new Scanner(System.in);
        int opcao, op2;
        File dir = new File(".");
        File newDir = new File(dir.getCanonicalPath()+"\\dados");
        if (!newDir.exists()){
            newDir.mkdir();
            System.out.println("Coloque algum arquivo para ser editado na pasta: "+newDir.getCanonicalPath());
            return;
        }
        
        System.out.println("Digite o nome do arquivo texto a ser utilizado, sem a extensão: ");
        String nome = arquivo.getInput();
        dir = new File(newDir.getCanonicalPath()+"\\"+nome+".txt");
        if(!dir.exists()){
            System.out.println("Arquivo nao encontrado");
            System.out.println("Coloque o arquivo: " + nome + ".txt " + " na diretorio -  " + newDir.getCanonicalPath());
            System.out.println("Só é possivel editar arquivos na pasta: " + newDir.getCanonicalPath());
            return;
        }

        arquivo.setNameFile(nome);
        arquivo.lerArquivo();
        arquivo.getIndicePrimario();
        String[] secKeyName = arquivo.getSecFieldNames();
        
        
        do {
            String param="";
            System.out.println("\tSistema de Consulta");
            System.out.println("1. Consultar");
            System.out.println("2. Incluir");
            System.out.println("3. Exclui");
            System.out.println("4. Visualizar Indices");
            System.out.println("5. Consultar (Chaves Secundaria)");
            System.out.println("0. Fim");
            System.out.println("Opcao: (Escolha um número relacioando ao que deseja executar)");
            opcao = entrada.nextInt();
            switch (opcao) {

                case 0:
                    break;
                case 1:
                    System.out.println("Digite chave primaria - Consultar");
                    param = arquivo.getInput();
                    arquivo.consultarRegistro(param);
                    break;
                case 2:
                    param = arquivo.getInputRegistro();
                    arquivo.inserirRegistro(param);
                    break;
                case 3:      
                    System.out.println("Digite chave primaria - Excluir");
                    param = arquivo.getInput();
                    arquivo.excluirRegistro(param);
                    break;
                case 4:
                    System.out.println("Imprimir Indices");
                    arquivo.getIndicePrimario();
                    arquivo.printIndiceSecundario();
                    break;
                case 5:
                    int i;
                    for(i = 0; i < secKeyName.length; i++){
                        System.out.println(i+". "+secKeyName[i]);
                    }
                    System.out.println("Gostaria de utilizar qual campo para pesquisa da chave secundaria:");
                    System.out.println("Opção: (Escolha o número relacionado a chave que deseja pesquisar)");
                    op2 = entrada.nextInt();
                    if(op2 >= 0 && op2 < i){
                        System.out.println("Digite chave secundaria - Consultar");
                        param = arquivo.getInput();
                        arquivo.consultarRegistroSec(param, op2);
                    }else
                        System.out.println("Opção de chave secundaria inválida.");
                        
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }
}
