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
 * Labeller.java
 *
 * Created on 30 novembre 2003, 11.38
 * @author  Valter Crescenzi, Luigi Arlotta
 */
package roadrunner.labeller;

import java.util.*;
import java.util.logging.*;

import roadrunner.config.Config;
import roadrunner.config.Constants;
import roadrunner.WrappersRepository;
import roadrunner.ast.*;
import roadrunner.parser.BindingException;
import roadrunner.Wrapper;
/**
 *
 * @author   Luigi Arlotta, Valter Crescenzi
 */
public class Labeller extends VisitorPlugger implements Visitor, Constants {
    
    static private Logger log = Logger.getLogger(Labeller.class.getName());
    
    private WrappersRepository repository;
    private VisualLabeller visualLabeller;
    private PrefixLabeller prefixLabeller;
    private LabelExtractor labelExtractor;
    private boolean prefixEnabled;
    private boolean visualEnabled;
        
    /** Creates a new instance of Labeller */
    public Labeller(WrappersRepository repository) throws BindingException {
        this.repository = repository;
        this.prefixEnabled = Config.getPrefs().getBoolean(PREFIX_ENABLED);
        this.visualEnabled = Config.getPrefs().getBoolean(VISUAL_ENABLED);
        this.labelExtractor = new LabelExtractor();
        init();
    }
    
    private boolean isLabellingEnabled() {
        return this.prefixEnabled || this.visualEnabled;
    }    
    private boolean isLabellingByPrefixEnabled() {
        return this.prefixEnabled;
    }
    private boolean isVisualLabellingEnabled() {
        return this.visualEnabled;
    }

    public void init() throws BindingException {
        Iterator it = this.repository.getWrappers().iterator();
        while (it.hasNext()) {
            // first set anonymous labels for all variants...
            Wrapper wrapper = (Wrapper)it.next();
            wrapper.getExpression().getRoot().jjtAccept(
            new VisitorPlugger() {
                private int counter = 0;
                public boolean visit(ASTVariant variant) {
                    variant.setLabel(getAnonymousLabel(counter++));
                    return true;
                }
            }
            );
            // ...then set all labels it can guess
            Set instances = this.repository.getInstances(wrapper);
            if (isLabellingByPrefixEnabled()) {
                this.prefixLabeller = new PrefixLabeller(wrapper,instances);
            }
            if (isVisualLabellingEnabled()) {
                this.visualLabeller = new VisualLabeller(wrapper,instances);
            }
            if (isLabellingEnabled()) {
                wrapper.getExpression().getRoot().jjtAccept(this); //go labeller, go!
            }
        }
    }
    
    private String getAnonymousLabel(int attNumber) {
        final int range = ((int)'Z'-(int)'A')+1;
        int letter = attNumber % range;
        int number = attNumber / range;
        return "_"+((char)(letter+(int)'A'))+(number==0?"":Integer.toString(number))+"_";
    }
    
    public boolean visit(ASTVariant node) {
        String label = guessLabel(node);
        if (label!=null) node.setLabel(label);
        return true;
    }
        
    private String guessLabel(ASTVariant node) {
        // we can guess only label of text tokens
        if (!node.getToken().isPCDATA()) return null; 
        String label = null;
        //look for  constant texts whose occurrences are visually close to the occurrences of the variant values
        if (isVisualLabellingEnabled()) {
            label = this.visualLabeller.getLabel(node);
        }
        //look for a common suffix or prefix amongst all the values of a variants        
        if (label==null && isLabellingByPrefixEnabled()) {
            label = this.prefixLabeller.getLabel(node);
        }
        return this.labelExtractor.extractLabel(label);
    }
    
    
}
