import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class KnowledgeBase {
	
	HashSet<Formula> kb;
	
	KnowledgeBase(HashSet<Formula> kb){
		this.kb = kb;
	}
	
	public boolean remove(Formula f){
		return kb.remove(f);
	}
	
	public void tell(Formula f){
		if (!kb.contains(f)){
			kb.add(f);
		}
	}
	
	public void tell(ArrayList<Formula> f){		
		for (int i = 0; i < f.size(); i++) {
			tell(f.get(i));
		}		
	}
	
	public boolean ask(Literal[] f){
		boolean result = true;
		
		for (int i = 0; i < f.length && result; i++) {
			result = ask(f[i]);
		}
		
		return result;
	}
	
	public boolean ask(Literal f){
		return kb.contains(f) || backwardChaining(f);
	}	

	private boolean backwardChaining(Literal l) {
		Stack<Literal> agenda 	= new Stack<Literal>();
		ArrayList<Literal> facts 	= new ArrayList<Literal>();
		ArrayList<Implication> clauses 	= new ArrayList<Implication>();
		HashMap<Literal,Implication> entailed = new HashMap<Literal,Implication>();
		init(facts, clauses);
		agenda.add(l);
		
		while (!agenda.isEmpty()){
			Literal q = agenda.pop();
			if (!facts.contains(q)){
				HashMap<Literal, Implication> p = new HashMap<Literal,Implication>();
				for ( Implication clause :  clauses ){
					if ( clause.match(q) ){
						Literal[] conjuncts = clause.getPreconditions();
						for(int i=0;i<conjuncts.length;i++){
								if (!agenda.contains(conjuncts[i])){
									p.put(conjuncts[i], clause);
								}
						}
					}
				}
				
				if (p.isEmpty()){
					return false;
				}else{
					for (Literal pi : p.keySet() ){
						if ( !entailed.keySet().contains(pi)){
							entailed.put(pi, p.get(pi));
							agenda.push(pi);
						}
					}
				}
			}
		}
		
		for (Literal ei : entailed.keySet()){
			showExplanation(ei, entailed.get(ei) ) ;
		}
		return true;
		//=====================================================================
		//                      STOP MODIFYING HERE 
		//=====================================================================		
	}
		
	private void init( ArrayList<Literal> facts, ArrayList<Implication> clauses ){
		for( Formula sentence : kb){
			if (sentence.isImplication()){
				clauses.add((Implication)sentence);
			}else{
				facts.add((Literal)sentence);
			}
		}
	}

	@SuppressWarnings("unused")
	private void showExplanation(Literal l, Formula f) {
		//System.out.println("I deduced " + l + " because of\t" + f);		
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder("==============  KB  ==============\n");
		for (Formula a : kb) {
			result.append(a.toString() + "\n");
		}
				
		return result.append("============== END OF KB  ==============\n").toString();
	}

}
