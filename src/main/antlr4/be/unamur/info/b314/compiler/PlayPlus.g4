grammar PlayPlus;

import PlayPlusWords;

root: instruction+;

instruction: AFFECT LPAR ID COMMA expression RPAR   #affectInstr
           ;

expression: NUMBER                                  #constantExpr
          | ID                                      #variableExpr
          | left=expression op=(PLUS|MINUS) right=expression   #plusMinusExpr
          ;
