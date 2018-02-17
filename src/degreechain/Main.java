package degreechain;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import degreechain.core.Block;
import degreechain.core.Transaction;
import degreechain.core.TransactionOutput;
import degreechain.core.Wallet;
import degreechain.networking.ExecuteCommands;
import degreechain.util.ChainUtils;
import degreechain.util.Parameters;

public class Main {

    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<>();
    public static float minimumTransaction = 0.1f;

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        //add our blocks to the blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        //Create wallets:
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet wallet = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        Transaction genesisTransaction = new Transaction(wallet.getPublicKey(), walletA.getPublicKey(), 100f, null);
        genesisTransaction.generateSignature(wallet.getPrivateKey());	 //manually sign the genesis transaction
        genesisTransaction.setTransactionId("0"); //manually set the transaction id
        genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getReciepient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId())); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.getOutputs().get(0).id, genesisTransaction.getOutputs().get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        addBlock(block3);


        if(!ChainUtils.isChainValid(Parameters.blockchain, genesisTransaction)) {
            System.out.println("Not Valid Chain!!");
            return;
        }

        ExecuteCommands server = new ExecuteCommands(8888);
        ExecuteCommands client = new ExecuteCommands(8889);
        ExecuteCommands client2 = new ExecuteCommands(8890);
        client.connect("127.0.0.1", 8888);
        client2.connect("127.0.0.1", 8888);

        System.out.println("client"+client.getBlockChainSize());
        System.out.println("client2"+client2.getBlockChainSize());
        server.connect("127.0.0.1", 8889);
        System.out.println("server"+server.getBlockChainSize());

    }

    private static void addBlock(Block newBlock) {
        newBlock.mineBlock(Parameters.difficulty);
        Parameters.blockchain.add(newBlock);
    }
}