grammar Slip;

import SlipWords;


program : (prog | map) EOF;

map : 'map' ':' NAT NAT line+;

line : ('@' | 'X' | 'G' | 'P' | 'A' | 'B' | 'T' | 'S' | '_' | 'Q')+;

prog : impDecl (enumDecl | varDecl | funcDecl | constDecl)* mainDecl;

mainDecl : 'main' 'as' 'function' '(' ')' ':' 'void' 'do' ((varDecl | instruction)* dig ';' (varDecl | instruction)*) 'end';

instBlock : (varDecl | enumDecl | constDecl)* instruction+;

argList : varDef(',' varDef)*;

funcDecl : ID 'as' 'function' '(' (argList)? ')' ':' funcType 'do' (instBlock)+ 'end';

funcType : scalar | VOIDTYPE;

impDecl : '#' 'import' FILENAME;

type : scalar
     | array
     | structure
     ;


scalar : BOOLEANTYPE
       | INTEGERTYPE
       | CHARTYPE
       ;

number : ('-')? NAT;

array : scalar'[' number (',' number)? ']';

structure : 'record' (varDecl)+ 'end';

varDecl : varDef ('=' initVar)? ';';

varDef : ID (',' ID)* 'as' type;

initVar : exprD
        | initArrays
        ;

initArrays : '(' (initVar (',' initVar)*)?')';

constDecl : 'const' ID 'as' type '=' initVar ';';

enumDecl : 'enum' ID '=' '(' ID (',' ID)* ')' ';';

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