package degreechain.core;

public class TransactionInput {
	
	public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
	public TransactionOutput UTXO; //Contains the Unspent transaction output
	
	public TransactionInput(){}
	
	TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

}
