grammar PlayPlus;

import PlayPlusWords;

root: instruction+;

instruction: AFFECT LPAR ID COMMA expression RPAR   #affectInstr
           ;

expression: NUMBER                                  #constantExpr
          | ID                                      #variableExpr
          | expression op=(PLUS|MINUS) expression   #plusMinusExpr
          ;
