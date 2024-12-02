package c.block;
import c.block.api.IitisLabsAPIService;
import c.block.model.Block;
import c.block.model.TsResponse;
import c.block.model.util.BlockUtil;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.security.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static c.block.Secure.PASSWORD;
import static c.block.model.util.BlockUtil.calculateHash;

public class Blockchain {
    private List<Block> chain = new ArrayList<>();
    private PublicKey ownerPublicKey;
    private PrivateKey ownerPrivateKey;
    private Connection dbConnection;

    public Blockchain() throws Exception {
        generateKeys();
        connectToDatabase();
        loadChainFromDatabase();
        validateBlockchain();
    }

    private void connectToDatabase() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/blockchain_db";
        String user = "postgres";
        String password = PASSWORD;
        dbConnection = DriverManager.getConnection(url, user, password);
    }

    private void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        ownerPublicKey = keyPair.getPublic();
        ownerPrivateKey = keyPair.getPrivate();
    }

    private void loadChainFromDatabase() throws Exception {
        String query = "SELECT * FROM blockchain ORDER BY id ASC";
        try (Statement stmt = dbConnection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String data = rs.getString("data");
                String previousHash = rs.getString("previous_hash");
                String blockHash = rs.getString("block_hash");
                byte[] dataSignature = rs.getBytes("data_signature");
                byte[] blockHashSignature = rs.getBytes("block_hash_signature");
                String ts = rs.getString("ts");

                Block block = new Block(data, previousHash, blockHash, dataSignature, blockHashSignature, ts);
                chain.add(block);
            }
        }
    }

    public void addBlock(String data) throws Exception {
        Block previousBlock = chain.isEmpty() ? null : chain.get(chain.size() - 1);
        String previousHash = previousBlock == null ? null : previousBlock.getBlockHash();
        Block newBlock = new Block(data, previousHash, ownerPrivateKey);

        saveBlockToDatabase(newBlock);
        chain.add(newBlock);
    }

    public void addBlockWithArbiter(String data) throws Exception {
        Block previousBlock = chain.isEmpty() ? null : chain.get(chain.size() - 1);
        String previousHash = previousBlock == null ? null : previousBlock.getBlockHash();
        String hash = calculateHash(previousHash, data);
        String hexEncodedHash = String.format("%040x", new BigInteger(1, hash.getBytes()));
        TsResponse ts = IitisLabsAPIService.ts(hexEncodedHash);
        Block newBlock = new Block(data, previousHash, ownerPrivateKey, ts.getTimeStampToken().getTs(), ts.getTimeStampToken().getSignature());

        saveBlockToDatabase(newBlock);
        chain.add(newBlock);
    }

    private void saveBlockToDatabase(Block block) throws SQLException {
        String query = "INSERT INTO blockchain (data, previous_hash, block_hash, data_signature, block_hash_signature, ts) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            pstmt.setString(1, block.getData());
            pstmt.setString(2, block.getPreviousHash());
            pstmt.setString(3, block.getBlockHash());
            pstmt.setBytes(4, block.getDataSignature());
            pstmt.setBytes(5, block.getBlockHashSignature());
            pstmt.setString(6, block.getTs());
            pstmt.executeUpdate();
        }
    }

    public void printBlockchain() {
        for (Block block : chain) {
            System.out.println(block);
        }
    }

    @SneakyThrows
    public void validateBlockchain() {
        try {
            for (int i = 0; i < chain.size(); i++) {
                Block currentBlock = chain.get(i);

                // verify signature with api
                String pubKey = IitisLabsAPIService.publicKey64();
                PublicKey key = BlockUtil.getKey(pubKey);
                Signature signature = Signature.getInstance("SHA256withRSA");
                signature.initVerify(key);
                boolean verify = signature.verify(currentBlock.getBlockHashSignature());
                System.out.println(verify);

                String calculatedHash = calculateHash(currentBlock);
                if (i > 0) {
                    Block previousBlock = chain.get(i - 1);
                    if (!currentBlock.getPreviousHash().equals(previousBlock.getBlockHash())) {
                        System.out.println("Block " + i + " previous hash does not match!");
                        throw new RuntimeException("Blockchain validation failed: broken chain at index " + i);
                    }
                }
            }
            System.out.println("Blockchain is valid.");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error during blockchain validation", e);
        }
    }


    public static void main(String[] args) throws Exception {
        Blockchain blockchain = new Blockchain();
        blockchain.addBlockWithArbiter("First Transaction");
        blockchain.addBlockWithArbiter("Second Transaction");
        blockchain.addBlockWithArbiter("Third Transaction");
        blockchain.printBlockchain();
    }
}
