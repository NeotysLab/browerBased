package com.neotys.htmlunit.customActions;

import com.google.common.base.Optional;
import com.neotys.action.result.ResultFactory;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.htmlunit.HtmlUnitUtils.BrowserContext;
import com.neotys.htmlunit.HtmlUnitUtils.HttpExeption;
import com.neotys.htmlunit.HtmlUnitUtils.NeoLoadBrowserEngine;

import java.util.List;
import java.util.Map;

import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;

public class CloseBrowserActionEngine implements ActionEngine {


    private static final String STATUS_CODE_INVALID_PARAMETER = "NL-BT-CLOSE_ACTION-01";
    private static final String STATUS_CODE_TECHNICAL_ERROR = "NL-BT-CLOSE_ACTION-02";
    private static final String STATUS_CODE_BAD_CONTEXT = "NL-BT-CLOSE_ACTION-03";
    private NeoLoadBrowserEngine neoLoadBrowserEngine;

    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();

        final Map<String, Optional<String>> parsedArgs;
        try {
            parsedArgs = parseArguments(parameters, CloseBrowserOption.values());
        } catch (final IllegalArgumentException iae) {
            return ResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
        }





        final Optional<String> tracemode=parsedArgs.get((CloseBrowserOption.TraceMode.getName()));


        final Logger logger = context.getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug("Executing " + this.getClass().getName() + " with parameters: "
                    + getArgumentLogString(parsedArgs, CloseBrowserOption.values()));
        }



        try {

            neoLoadBrowserEngine= (NeoLoadBrowserEngine) context.getCurrentVirtualUser().get("NeoLoadBrowser");
            if(neoLoadBrowserEngine==null) {

                return ResultFactory.newErrorResult(context, STATUS_CODE_BAD_CONTEXT, "NO Browser opened ");
            }
            else
            {
                neoLoadBrowserEngine.getContext().setContext(context);
                neoLoadBrowserEngine.getContext().setTracemode(tracemode);

            }
            sampleResult.sampleStart();
            neoLoadBrowserEngine.closeBrowser();
            context.getCurrentVirtualUser().remove("NeoLoadBrowser");
            sampleResult.sampleEnd();

        }

        catch(Exception e)
        {
            return  ResultFactory.newErrorResult(context, STATUS_CODE_TECHNICAL_ERROR,  "HTtml Unit technical Error ", e);
        }

        sampleResult.setRequestContent(requestBuilder.toString());
        sampleResult.setResponseContent(responseBuilder.toString());


        return sampleResult;
    }

    private void appendLineToStringBuilder(final StringBuilder sb, final String line){
        sb.append(line).append("\n");
    }

    /**
     * This method allows to easily create an error result and log exception.
     */
    private static SampleResult getErrorResult(final Context context, final SampleResult result, final String errorMessage, final Exception exception) {
        result.setError(true);
        result.setStatusCode("NL-HtmlUnitOpenPage_ERROR");
        result.setResponseContent(errorMessage);
        if(exception != null){
            context.getLogger().error(errorMessage, exception);
        } else{
            context.getLogger().error(errorMessage);
        }
        return result;
    }

    @Override
    public void stopExecute() {
        // TODO add code executed when the test have to stop.
        neoLoadBrowserEngine.closeBrowser();
    }

}