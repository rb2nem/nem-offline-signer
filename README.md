# Offline Transactions Signer

## Why

Even if with NEM you can have high protection of your funds with multi-sig accounts, you still might want to 
keep the keys of your account offline. This is what is enabled by this project.

## Warning

This is work in progress and needs some polishing and testing. Use at your own risk.

## Installation and Running it

We suppose you will use this with  2 system: one offline where you sign the transactions, which we will call the signing system, and one online
for announcing transactions, which we will call the announcing system.

### Manual installation

If the signing system is online while you set it up, you can follow these instructions. If not, you need to bring all dependencies 
to the system. That might be easier with the jar mentioned below, but at a bigger risk for you as you didn't build the jar yourself.

This project was developed with Groovy. The easiest way to install it is with [sdkman](http://sdkman.io/). With sdkman installed, it is simply a matter of running
`sdk install groovy`.

You need `nem.core` and its dependencies on both systemss. For security reasons, you might want to compile nem.core from source.
You can find compilation instructions in the [NEM programming manual](https://github.com/rb2nem/nem_programming/blob/master/02-setting-up-environment.md#linux),
where it is also explained how to download all nem.core dependencies with `mvn dependency:copy-dependencies -DoutputDirectory=/root/.groovy/lib/`.

Once everything is set up, you can clone this directory and run the offline signer:
```
git clone https://github.com/rb2nem/nem-offline-signer.git
cd  src/main/groovy/
groovy OfflineSigner.groovy
```

### Jar download

To make it easier for your testing, a runnable jar is available at [Mega](https://mega.nz/#!xZZhzbgJ!ZYZL-tbdjtlnNPQI0yrfsnq6z_Ng1Glg15O0chto_dU).
Run it with `java -jar OfflineSigner.jar`


## Usage

### Creating the transaction offline

The OfflineSigner requires 4 pieces of information:

* private key in hex format
* recipient address
* amount in XEMs
* filename to use when writing transaction to disk.

For each transaction, 2 files with the extension respectively `.data` and `.sign`.
The first contains the transaction data, and the second the transaction signature.

Once these files are on the disk, you need to copy them to a system that is connected to the internet
to announce them to the NEM network.

### Announcing transactions

This is done with the script `online_transaction_announcer.groovy`, which requires argument 
as displayed in the help:
```
Usage: online_transaction_announcer.groovy -s server -f prefix
             -f,--filename <arg>   Transaction is saved in ${prefix}.sign and
                                   ${prefix}.data
             -h,--help             Show usage information
             -s,--server <arg>     Use NIS server to announce transaction

```
Using a textnet account signer, if you saved the transaction info in `tx.sign` and `tx.data` (meaning you used `tx` as filename in 
the OfflineSigner interface), you can now announce it with: 
```
groovy online_transaction_announcer.groovy -s bob.nem.ninja -f tx
```

## Future

This proff of concept can be greatly improved. If you're interested let me know at rb2nem@gmail.com.
If you like the tool and want to send some XEMs, do it to `NBXQF5-N5HXDX-OYEVA4-BP4CBJ-NBZSD2-AJH2JT-RII7`.
