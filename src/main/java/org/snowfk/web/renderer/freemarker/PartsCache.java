/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.web.renderer.freemarker;

import java.util.List;

import org.snowfk.web.part.Part;



public class PartsCache {
    
    private List<String> pris;
    private String prisUid;
    
    private List<Part> parts;
    private String href;


    
    
    /*--------- Getters & Setters ---------*/
    public List<String> getPris() {
        return pris;
    }

    public void setPris(List<String> pris) {
        this.pris = pris;
    }
    
    public String getPrisUid() {
        return prisUid;
    }

    public void setPrisUid(String prisUid) {
        this.prisUid = prisUid;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    



    
    /*--------- /Getters & Setters ---------*/
    
    
}
