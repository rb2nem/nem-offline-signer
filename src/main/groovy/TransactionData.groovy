import groovy.beans.Bindable  
@Bindable
class TransactionData {  
    String privateKey, recipient, filename, amount
    String toString() { "transactiondata[privateKey=---,recipient=$recipient,amount=$amount,filename=$filename]" }
}
  

