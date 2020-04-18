import java.io.*;

public class main {
    public static void main(String args[]){
        if(args[0].equals("-e")){
            encrypt(args[2], args[4], args[6]);
        }
        else if(args[0].equals("-d")){
            decrypt(args[2], args[4], args[6]);
        }
        else if(args[0].equals("-b")){
            hack(args[2], args[4], args[6]);
        }
        else{
            System.out.println("error");
        }
    }

    public static void encrypt(String keyPath, String originalPath, String destPath){
        File keyFile = new File(keyPath);
        File orgFile = new File(originalPath);
        byte[] keyBytesArray = new byte[48];
        byte[] orgBytesArray=new byte[(int)orgFile.length()];
        try {
            FileInputStream fileInputStreamKey = new FileInputStream(keyFile);
            fileInputStreamKey.read(keyBytesArray);
            FileInputStream fileInputStreamOrg = new FileInputStream(orgFile);
            fileInputStreamOrg.read(orgBytesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[][] key1=new byte[4][4];
        byte[][] key2=new byte[4][4];
        byte[][] key3=new byte[4][4];
        int index=0;
        index=fillMat(key1,keyBytesArray,index);
        index=fillMat(key2,keyBytesArray,index);
        fillMat(key3,keyBytesArray,index);
        int i=0;
        int j=0;
        byte[] encBytesArray=new byte[orgBytesArray.length];
        while(i<orgBytesArray.length){
            byte[][] encState=new byte[4][4];
            i=fillMat(encState,orgBytesArray,i);
            aes(encState,key1);
            aes(encState,key2);
            aes(encState,key3);
            j=fillArr(encState,encBytesArray,j);
        }
        File destFile = new File(destPath);
        try {
            destFile.createNewFile();
            FileOutputStream fileOutputStreamDest=new FileOutputStream(destFile);
            fileOutputStreamDest.write(encBytesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decrypt(String keyPath, String originalPath, String destPath){
        File keyFile = new File(keyPath);
        File orgFile = new File(originalPath);
        byte[] keyBytesArray = new byte[48];
        byte[] orgBytesArray=new byte[(int)orgFile.length()];
        try {
            FileInputStream fileInputStreamKey = new FileInputStream(keyFile);
            fileInputStreamKey.read(keyBytesArray);
            FileInputStream fileInputStreamOrg = new FileInputStream(orgFile);
            fileInputStreamOrg.read(orgBytesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[][] key1=new byte[4][4];
        byte[][] key2=new byte[4][4];
        byte[][] key3=new byte[4][4];
        int index=0;
        index=fillMat(key1,keyBytesArray,index);
        index=fillMat(key2,keyBytesArray,index);
        fillMat(key3,keyBytesArray,index);
        int i=0;
        int j=0;
        byte[] decBytesArray=new byte[orgBytesArray.length];
        while(i<orgBytesArray.length) {
            byte[][] encState = new byte[4][4];
            i = fillMat(encState, orgBytesArray, i);
            aesDec(encState,key3);
            aesDec(encState,key2);
            aesDec(encState,key1);
            j=fillArr(encState,decBytesArray,j);
        }
        File destFile = new File(destPath);
        try {
            destFile.createNewFile();
            FileOutputStream fileOutputStreamDest=new FileOutputStream(destFile);
            fileOutputStreamDest.write(decBytesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void hack(String plainTextPath, String cypherTextPath, String destPath){
        File plainFile = new File(plainTextPath);
        File cypherFile = new File(cypherTextPath);
        byte[] plainBytesArray=new byte[(int)plainFile.length()];
        byte[] cypherBytesArray=new byte[(int)cypherFile.length()];
        try {
            FileInputStream fileInputStreamKey = new FileInputStream(plainFile);
            fileInputStreamKey.read(plainBytesArray);
            FileInputStream fileInputStreamOrg = new FileInputStream(cypherFile);
            fileInputStreamOrg.read(cypherBytesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[][] key1=new byte[4][4];
        byte[][] key2=new byte[4][4];
        byte[][] key3=new byte[4][4];
        byte[][] plainState=new byte[4][4];
        byte[][] cypherState=new byte[4][4];
        fillMat(plainState,plainBytesArray,0);
        fillMat(cypherState,cypherBytesArray,0);
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                key1[i][j]=1;
                key2[i][j]=0;
            }
        }
        boolean key1isDistinct=false;
        boolean key2isDistinct=false;
        while(!key1isDistinct || !key2isDistinct){
            byte[][] testKey1=new byte[4][4];
            byte[][] testKey2=new byte[4][4];
            for(int i=0; i<4; i++){
                for (int j=0; j<4; j++){
                    key3[i][j]=plainState[i][j];
                    testKey1[i][j]=key1[i][j];
                    testKey2[i][j]=key2[i][j];
                }
            }
            shiftColumns(key3);
            shiftColumns(key3);
            shiftColumns(key3);
            shiftColumns(testKey1);
            shiftColumns(testKey1);
            shiftColumns(testKey2);
            addRoundKey(key3,cypherState);
            addRoundKey(key3,testKey1);
            addRoundKey(key3,testKey2);
            for(int i=0; i<4; i++){
                for (int j=0; j<4; j++){
                    if(key3[i][j]!=key1[i][j])
                        key1isDistinct=true;
                    if(key3[i][j]!=key2[i][j])
                        key2isDistinct=true;
                }
            }
            if(!key1isDistinct)
                key1[0][0]++;
            if(!key2isDistinct)
                key1[0][0]++;
        }
        byte[] keys=new byte[48];
        int index=0;
        index=fillArr(key1,keys,index);
        index=fillArr(key2,keys,index);
        fillArr(key3,keys,index);
        File destFile = new File(destPath);
        try {
            destFile.createNewFile();
            FileOutputStream fileOutputStreamDest=new FileOutputStream(destFile);
            fileOutputStreamDest.write(keys);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void aesDec(byte[][] state, byte[][] key){
        addRoundKey(state,key);
        shiftColumnsDec(state);
    }

    private static void aes(byte[][] state, byte[][] key){
        shiftColumns(state);
        addRoundKey(state,key);
    }

    private static void shiftColumnsDec(byte[][] state){
        for(int i=0; i<4; i++){
            for(int j=0; j<i; j++){
                byte tempByte=state[3][i];
                state[3][i]=state[2][i];
                state[2][i]=state[1][i];
                state[1][i]=state[0][i];
                state[0][i]=tempByte;
            }
        }
    }

    private static void shiftColumns(byte[][] state){
        for(int i=0; i<4; i++){
            for(int j=0; j<i; j++){
                byte tempByte=state[0][i];
                state[0][i]=state[1][i];
                state[1][i]=state[2][i];
                state[2][i]=state[3][i];
                state[3][i]=tempByte;
            }
        }
    }

    private static void addRoundKey(byte[][] state, byte[][] key){
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                state[i][j]=(byte)(state[i][j]^key[i][j]);
            }
        }
    }

    private static int fillMat(byte[][] mat, byte[] bytesArray, int index){
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                mat[j][i]=bytesArray[index];
                index++;
            }
        }
        return index;
    }

    private static int fillArr(byte[][] mat, byte[] bytesArray, int index){
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                bytesArray[index]=mat[j][i];
                index++;
            }
        }
        return index;
    }
}
