/*
     RoadRunner - an automatic wrapper generation system for Web data sources
     Copyright (C) 2003  Valter Crescenzi - crescenz@dia.uniroma3.it

     This program is  free software;  you can  redistribute it and/or
     modify it  under the terms  of the GNU General Public License as
     published by  the Free Software Foundation;  either version 2 of
     the License, or (at your option) any later version.

     This program is distributed in the hope that it  will be useful,
     but  WITHOUT ANY WARRANTY;  without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     General Public License for more details.

     You should have received a copy of the GNU General Public License
     along with this program; if not, write to the:

     Free Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

     ----

     RoadRunner - un sistema per la generazione automatica di wrapper su sorgenti Web
     Copyright (C) 2003  Valter Crescenzi - crescenz@dia.uniroma3.it

     Questo  programma è  software libero; è  lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come è pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma  è distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILITÀ  o di  APPLICABILITÀ PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne può
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
/*
 * TextToken.java
 *
 * Created on December 25, 2003, 12:22 PM
 * @author  Valter Crescenzi
 */

package roadrunner.parser.token;

import java.util.Map;

import roadrunner.ast.ASTToken;
import roadrunner.parser.Token;

class TextToken extends ASTToken implements Token {
    
    /** Thanks to these two inner classes we can mark TextToken and TagTag's constructors
     *  as private to avoid that Tokens are created bypassing this TokenFactory */
    private String text;

    /** Creates a new instance of TextToken */
    TextToken(String text, int depth) {
        super(depth,0);
        this.text = text;
    }
    
    public boolean isIMG()     { return false; }
    public boolean isLink()    { return false; }
    
    public String getText()    { return this.text; }
    public String getElement() { throw new RuntimeException("This object is supposed to be a text Token!"); }
    public Map getAttributes() { throw new RuntimeException("This object is supposed to be a text Token!"); }
    
    public String getVariantValue() { return this.getText(); }
    
    /* N.B. In order for these objects to be used both as tokens of samples and as leaves of expressions ,*/    
    /*  methods equals() and hashCode() must obey to the semantics of ASTToken's */
    public String toString()     { return getText();     }
}