grammar Slip;

import SlipWords;


program : (prog | map) EOF;

map : 'map' ':' NAT NAT line+;

line : ('@' | 'X' | 'G' | 'P' | 'A' | 'B' | 'T' | 'S' | '_' | 'Q')+;

prog : impDecl (declaration | funcDecl | constDecl)* mainDecl;

mainDecl : 'main' 'as' 'function' '(' ')' ':' 'void' 'do' ((declaration | instruction)* dig ';' (declaration | instruction)*) 'end';

instBlock : (declaration | constDecl)* instruction+;

argList : varDef(',' varDef)*;
varDef : ID (',' ID)* 'as' scalar;

funcDecl : ID 'as' 'function' '(' (argList)? ')' ':' funcType 'do' (instBlock)+ 'end';

funcType : scalar | VOIDTYPE;

impDecl : '#' 'import' FILENAME;

scalar : BOOLEANTYPE
       | INTEGERTYPE
       | CHARTYPE
       ;

number : ('-')? NAT;

declaration: (varDecl | arrayDecl | structDecl) ';';
varDecl : ID (',' ID)* 'as' scalar ('=' exprD)?;
arrayDecl : ID (',' ID)* 'as' scalar '[' number (',' number)* ']' ('=' initArrays)?;
structDecl : ID (',' ID)* 'as' 'record' (declaration)+ 'end';

initVar : exprD
        | initArrays
        ;

initArrays : '(' (initVar (',' initVar)*)?')';

constDecl : 'const' (constVar | constArray | constStruct) ';';
constVar : ID 'as' scalar '=' exprD;
constArray : ID 'as' scalar '[' number (',' number)* ']' '=' initArrays ;
constStruct : ID 'as' 'record' (declaration)+ 'end';

exprD : STRING                                                  # string
      | CHAR                                                    # char
      | '(' exprD ')'                                           # parens
      | exprG                                                   # exprGExpr
      | ID'(' (exprD (','exprD)*)? ')'                          # funcExpr

      // exprEnt copied here to avoid indirect recursion
      | number                                                  # intExpr
      | '-' exprD                                               # unaryMinusExpr
      | exprD ('*' | '/' | '%') exprD                           # timesDivideExpr
      | exprD ('+' | '-') exprD                                 # plusMinusExpr

      // exprBool copied here to avoid indirect recursion
      | 'true'                                                  # trueExpr
      | 'false'                                                 # falseExpr
      | exprD op=('=' | '<' | '>' | '<=' | '>=' | '<>') exprD   # comparExpr
      | exprD op=('and' | 'or') exprD                           # andOrExpr
      | 'not' exprD                                             # notExpr
      ;


exprG : ID                                                      # leftExprID
      | ID'['exprD (','exprD)? ']'                              # leftExprArray
      | exprG'.'ID                                              # leftExprRecord
      ;

instruction : 'if' '(' exprD ')' 'then' instruction+ 'end'                      # ifThenInstr
            | 'if' '(' exprD ')' 'then' instruction+ 'else' instruction+ 'end'  # ifThenElseInstr
            | 'while' '(' exprD ')' 'do' instruction+ 'end'                     # whileInstr
            | 'repeat' instruction+ 'until' '(' exprD ')' 'end'                 # untilInstr
            | 'for' ID ':=' exprD 'to' exprD 'do' instruction+ 'end'            # forInstr
            | exprG ':=' exprD ';'                                              # assignInstr
            | actionType ';'                                                    # actionInstr
            ;

actionType : 'left' '(' (exprD)? ')'
           | 'right' '(' (exprD)? ')'
           | 'up' '(' (exprD)? ')'
           | 'down' '(' (exprD)? ')'
           | 'jump' '(' (exprD)? ')'
           | 'fight' '(' ')'
           | dig
           ;

dig : 'dig' '(' ')';