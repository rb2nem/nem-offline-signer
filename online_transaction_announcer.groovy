import org.nem.core.connect.client.DefaultAsyncNemConnector;
import org.nem.core.connect.HttpMethodClient;
import org.nem.core.connect.ErrorResponseDeserializerUnion;
import org.nem.core.node.NodeEndpoint;
import org.nem.core.connect.client.NisApiId;
import org.nem.core.model.ncc.NemAnnounceResult
import org.nem.core.connect.HttpJsonPostRequest
import org.nem.core.node.NisPeerId;
import org.nem.core.model.ncc.RequestAnnounce
import org.nem.core.model.Account;

import java.lang.*;
import java.text.*;



filename=this.args[0]
def cli = new CliBuilder(usage: 'online_transaction_announcer.groovy -s server -f prefix')
cli.with {
  h longOpt: 'help', 'Show usage information'
  s longOpt: 'server', args: 1,   'Use NIS server to announce transaction'
  f longOpt: 'filename', args:1 , 'Transaction is saved in ${prefix}.sign and ${prefix}.data'
}

def options = cli.parse(args)

options.h && println("help")
!options.f  && println("missing f")
!options.s && println("missing server")
if ((options.h) || (! options.s || ! options.f)) {
  println "Both -s and -f are mandatory"
  cli.usage()
  return
}

server=options.s
filename=options.f

// setup NIS communications
node = NodeEndpoint.fromHost(server);
http = new HttpMethodClient<ErrorResponseDeserializerUnion>();
conn = new DefaultAsyncNemConnector<NisPeerId>(http,{ e -> throw new RuntimeException(e.toString()) ;});
conn.setAccountLookup(Account.metaClass.&invokeConstructor  )

// serialize transaction
final byte[] transferBytes = new File("${filename}.data").bytes

final byte[] signature = new File("${filename}.sign").bytes

// sign transaction and send to nis
final RequestAnnounce announce = new RequestAnnounce(
					transferBytes,
					signature);
f = conn.postAsync( node,
		    NisApiId.NIS_REST_TRANSACTION_ANNOUNCE,
		    new HttpJsonPostRequest(announce))

// get the future's value, and construct the announce result
final NemAnnounceResult result = new NemAnnounceResult(f.get());
if (result.isError()) {
	println "ERROR"
	println result.getCode()
	println result.getMessage()

} else {
	println result.getTransactionHash()
}
println "done, if the script does not ext, you can press CTRL-C to shut it down"
