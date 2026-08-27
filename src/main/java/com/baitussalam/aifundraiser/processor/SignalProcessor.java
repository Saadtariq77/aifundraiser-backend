package com.baitussalam.aifundraiser.processor;

import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.entity.OpportunitySignal;

public interface SignalProcessor {

    boolean supports(ExternalApiRawData rawData);

    OpportunitySignal process(ExternalApiRawData rawData);
}