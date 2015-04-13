module Relation
(isreflexive
, issymmetric
, isonto
, isonetoone
, istransitive
, isfunct
, partitions
, topairs
) where
import Data.List
-- | True if the relation is reflexive
isreflexive :: [(Int, Int)] -> Bool
isreflexive arr = and [elem (x, x) arr | x <- ar] where ar = (map fst arr) ++ (map snd arr)
-- | True if the relation is symmetric
issymmetric :: [(Int, Int)] -> Bool
issymmetric arr = and [elem (y, x) arr | (x, y) <- arr]
-- | True if the relation is onto.
isonto :: [(Int, Int)] -> Bool
isonto arr = and (map (\x -> elem x (map snd arr)) [1..(fst(head arr))]) 
-- | True if the relation is one to one
isonetoone :: [(Int, Int)] -> Bool
isonetoone arr = nodup (sort (map snd arr))

nodup :: [Int] -> Bool
nodup arr = if length arr < 2 then True else ((head arr) < (arr !! 1)) && (nodup (tail arr))
-- | True if the relation is transitive
istransitive :: [(Int, Int)] -> Bool
istransitive arr = and [elem (x, z) arr | (x, a) <- arr, (b, z) <- arr, a==b] 
-- | True if the relation is a function
isfunct :: [(Int, Int)] -> Bool
isfunct arr = nodup (sort (map fst arr))
-- | The equivalence partitions of a relation, assuming it is an equivalence class
partitions :: [(Int, Int)] -> [[Int]]
partitions arr = partitionsW arr []

partitionsW :: [(Int, Int)] -> [[(Int)]] -> [[(Int)]]
partitionsW s acc = if length s == 0 then acc else partitionsW (filter (\x -> notElem (fst x) (map fst (fst n))) (snd n)) (acc ++ [(map fst (fst n))]) where n = partition (\x -> (snd x) == (snd(head s))) s

parseint :: String -> Int
parseint x  = read x ::Int

topair :: String -> (Int, Int)
topair s = (parseint (head x), parseint (last x)) where x = (words s)
-- | turn a String of the form s+\n[\d+ \d+\n]+ into a list of pairs
topairs :: String -> [(Int, Int)]
topairs s = map topair (tail (lines s))

