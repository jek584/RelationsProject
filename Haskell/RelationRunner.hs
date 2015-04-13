import Relation
handle :: [[Int]] -> String
handle arr = if length arr > 10 then show (length arr) ++ " partitions!\n" else unlines (["Partitions: "] ++ (map show arr))
main = do
	input <- getContents
	let pairs = (topairs input)
	let sym = issymmetric pairs
	let trans = istransitive pairs
	let ref = isreflexive pairs
	--putStr("Symmetric: " ++ (show sym) ++ "\n")
	--putStr("Transitive: " ++ (show trans) ++ "\n")
	--putStr("Reflexive: " ++ (show ref) ++ "\n")
	if sym && trans && ref
	then putStr(handle (partitions pairs))
	else putStr("Not an equivalence class\n")	
	--let funct = isfunct pairs
	--let onetoone = isonetoone pairs
	--let onto = isonto pairs
	--putStr("Function: " ++ (show funct) ++ "\n")
	--putStr("One To One: " ++ (show onetoone) ++ "\n")
	--putStr("Onto: " ++ (show onto) ++ "\n")		
