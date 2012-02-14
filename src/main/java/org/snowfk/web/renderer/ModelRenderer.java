package org.snowfk.web.renderer;

import java.io.Writer;



public interface ModelRenderer{
    
    public void render(Object model, Writer out);
    
}