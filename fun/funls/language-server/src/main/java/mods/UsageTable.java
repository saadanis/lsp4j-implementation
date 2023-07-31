package mods;

import java.util.HashMap;

import org.antlr.v4.runtime.ParserRuleContext;

public class UsageTable {
	
	private HashMap<String, ParserRuleContext> globals, locals;
		
	public UsageTable() {
		
		globals = new HashMap<String,ParserRuleContext>();
		locals = null;
	}
	
	public void put(String id, ParserRuleContext ctx) {
		
		if (!id.equals("main")) {
			HashMap<String,ParserRuleContext> scope = (locals != null ? locals : globals);
			scope.put(id, ctx);
		}
	}
	
	public void use(String id) {
		
		if (locals != null && locals.get(id) != null)
			locals.remove(id);
		else
			globals.remove(id);
	}
	
	public void enterScope() {
		
		locals = new HashMap<String, ParserRuleContext>();
	}
	
	public HashMap<String,ParserRuleContext> exitScope() {
		
		HashMap<String,ParserRuleContext> unused = new HashMap<String,ParserRuleContext>();
		
		if (locals != null) {
			locals.forEach((id, ctx) -> {
				unused.put(id, ctx);
			});
			locals = null;
		} else {
			globals.forEach((id, ctx) -> {
				unused.put(id, ctx);
			});
		}
		
		return unused;
	}
}
