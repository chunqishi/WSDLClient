package org.lappsgrid.wsdlclient;

import com.eviware.soapui.impl.wsdl.*;
import com.eviware.soapui.impl.wsdl.support.wsdl.WsdlImporter;
import com.eviware.soapui.model.iface.Operation;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.Submit;
import com.eviware.soapui.support.SoapUIException;

import java.io.IOException;

/**
 * Created by lapps on 4/2/2015.
 */
public class SoapUIClient {

    public final static void main(String [] args) throws Exception {
        WsdlProject project = new WsdlProject();
        WsdlInterface[] wsdls = WsdlImporter.importWsdl(project, "http://www.webservicex.com/globalweather.asmx?WSDL");
        WsdlInterface wsdl = wsdls[0];
        for (Operation operation : wsdl.getOperationList()) {
            WsdlOperation op = (WsdlOperation) operation;
            System.out.println("OP:"+op.getName());
            System.out.println(op.createRequest(true));
            System.out.println("Response:");
            System.out.println(op.createResponse(true));

            WsdlOperation wsdlOperation = (WsdlOperation) operation;
            // create a new empty request for that operation
            WsdlRequest request = wsdlOperation.addNewRequest("My request");
            request.setTimeout("2000");
            String requestContent = wsdlOperation.createRequest(true);
            request.setRequestContent(requestContent);
            System.out.println("REQUEST: " + requestContent);
            // submit the request
            try {
                WsdlSubmit submit = (WsdlSubmit) request.submit(new WsdlSubmitContext(request), false);
                Submit.Status status = submit.getStatus(); //FINISHED OR ERROR
                System.out.println("STATUS: " + status);
                Response response = submit.getResponse();
                System.out.println("RESPONSE: " + response.getContentAsString());
            } catch (Request.SubmitException ex) {
                //Catch the exception
            }
        }
    }

}
