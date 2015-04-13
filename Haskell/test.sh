ghc -o Relation Relation.hs
ghc -o RelationRunner RelationRunner.hs
../testing2/eq 15 | ./RelationRunner
