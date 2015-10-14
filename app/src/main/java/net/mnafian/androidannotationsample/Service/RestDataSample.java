package net.mnafian.androidannotationsample.Service;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

/**
 * Created by mnafian on 14/10/15.
 */
@Rest(rootUrl = "http://www.mnafian.net", converters = { StringHttpMessageConverter.class })
@Accept(MediaType.APPLICATION_JSON)
public interface RestDataSample {
    @Get("/?json=1")
    String getDataSample();
}
