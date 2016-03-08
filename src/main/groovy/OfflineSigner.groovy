import org.nem.core.model.Account;
import org.nem.core.model.TransferTransaction;
import org.nem.core.time.UnixTime;
import org.nem.core.crypto.KeyPair
import org.nem.core.crypto.PrivateKey
import org.nem.core.model.primitive.Amount
import org.nem.core.model.TransferTransactionAttachment
import org.nem.core.serialization.BinarySerializer
import org.nem.core.crypto.Signer
import org.nem.core.model.Address;
import org.nem.core.time.SystemTimeProvider

import java.lang.*;

import groovy.swing.SwingBuilder  
import static javax.swing.JFrame.EXIT_ON_CLOSE  
import java.awt.*
import TransactionData
 
class OfflineSigner {
  public static void main(String[] args){
    buildGui()
  }

def save_transaction(tx) {
  // TimeInstant for transaction deadline, set 24 hours in the future
  timeProvider=new SystemTimeProvider()
  ts_ti=timeProvider.getCurrentTime().addHours(24);

  // recipient, from destination address (account2)
  recipient_account=new Account(Address.fromEncoded(tx.recipient.replaceAll("-","").trim()))

  // sender, from private key string (account1)
  private_key= PrivateKey.fromHexString(tx.privateKey)
  sender_key_pair= new KeyPair(private_key)
  sender_account=new Account(sender_key_pair)


  // amount in microXEMs
  amount = new Amount(tx.amount.toInteger()*1000000);

  // empty attachment
  attach = new TransferTransactionAttachment()

    // create transaction instance and set its deadline
    transaction=new TransferTransaction(timeProvider.getCurrentTime(),
        sender_account,
        recipient_account,
        amount,
        attach)
    transaction.setDeadline(ts_ti);

  // serialize transaction
  final byte[] transferBytes = BinarySerializer.serializeToBytes(transaction.asNonVerifiable());
  final Signer signer = transaction.getSigner().createSigner();

  println tx.filename+".data"
  new File(tx.filename+".data").bytes = transferBytes
  println tx.filename+".sign"
  new File(tx.filename+".sign").bytes = signer.sign(transferBytes).getBytes()

}
  
  public static void buildGui() {
  def tx = new TransactionData(privateKey: '', recipient: '', amount: 0, filename: '')
  def swingBuilder = new SwingBuilder()
  swingBuilder.edt {  // edt method makes sure UI is build on Event Dispatch Thread.
      lookAndFeel 'nimbus'  // Simple change in look and feel.
      frame(title: 'Transaction', size: [350, 230], 
              show: true, locationRelativeTo: null, 
              defaultCloseOperation: EXIT_ON_CLOSE) { 
          borderLayout(vgap: 5)
          
          panel(constraints: BorderLayout.CENTER, 
                  border: compoundBorder([emptyBorder(10), titledBorder('Enter your transaction data:')])) {
              tableLayout {
                  tr {
                      td {
                          label 'Your private key:'  // text property is default, so it is implicit.
                      }
                      td {
                          textField id: 'privateKey', columns: 20, text: tx.privateKey
                      }
                  }
                  tr {
                      td {
                          label 'Recipient:'
                      }
                      td {
                          textField id: 'recipient', columns: 32, text: tx.recipient
                      }
                  }
                  tr {
                      td {
                          label 'amount:'
                      }
                      td {
                          textField id: 'amount', columns: 20, text: tx.amount
                      }
                  }
                  tr {
                      td {
                          label 'filename:'
                      }
                      td {
                          textField id: 'filename', columns: 20, text: tx.filename
                      }
                  }
              }
              
          }
          
          panel(constraints: BorderLayout.SOUTH) {
              button text: 'Save', actionPerformed: {
                  save_transaction(tx)
              }
          }
          
          // Binding of textfield's to address object.
          bean tx, 
              recipient: bind { recipient.text }, 
              privateKey: bind { privateKey.text }, 
              amount: bind { amount.text },
              filename: bind { filename.text }
      }  
  }
}


} //class
