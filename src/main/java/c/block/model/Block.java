package c.block.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.security.PrivateKey;

import static c.block.model.util.BlockUtil.calculateHash;
import static c.block.model.util.BlockUtil.signData;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Block {
    String data;
    String previousHash;
    String blockHash;
    byte[] dataSignature;
    byte[] blockHashSignature; // arbiter signature
    String ts;

    public Block(String data, String previousHash, PrivateKey privateKey) throws Exception {
        this.data = data;
        this.previousHash = previousHash == null ? "" : previousHash;
        this.blockHash = calculateHash(previousHash, data);
        // call api for signature
        this.dataSignature = signData(data, privateKey);
        this.blockHashSignature = signData(blockHash, privateKey);
        this.ts = "no-data";
    }

    public Block(String data, String previousHash, PrivateKey privateKey, String ts, String dataSignature) throws Exception {
        this.data = data;
        this.previousHash = previousHash == null ? "" : previousHash;
        this.blockHash = calculateHash(previousHash, data);
        // call api for signature
        this.dataSignature = signData(data, privateKey);
        this.blockHashSignature = dataSignature.getBytes();
        this.ts = ts;
    }

    public Block(String data, String previousHash, String blockHash, byte[] dataSignature, byte[] blockHashSignature, String ts) throws Exception {
        this.data = data;
        this.previousHash = previousHash;
        this.blockHash = blockHash;
        this.dataSignature = dataSignature;
        this.blockHashSignature = blockHashSignature;
        this.ts = ts;
    }

    public String getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public byte[] getDataSignature() {
        return dataSignature;
    }

    public byte[] getBlockHashSignature() {
        return blockHashSignature;
    }

    @Override
    public String toString() {
        return "Block{" +
                "data='" + data + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", blockSignatureFromArbiter='" + blockHashSignature + '\'' +
                ", timestamp='" + ts + '\'' +
                '}';
    }
}
