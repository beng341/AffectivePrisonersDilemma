#! /bin/bash
cd ~/NetBeansProjects/AffectivePrisonersDilemma/build/classes

EPA_COUNT=1
EPA_SEEDS=()
until [[ $EPA_COUNT -gt 9 ]]; do
	EPA_SEEDS+=($RANDOM)
	let EPA_COUNT=EPA_COUNT+1
done

echo "${EPA_SEEDS[*]}"

SEED_COUNT=1
SEEDS=()
until [[ $SEED_COUNT -gt 20 ]]; do
	SEEDS+=($RANDOM)
	let SEED_COUNT=SEED_COUNT+1
done
echo "${SEEDS[*]}"

for (( i = 0; i < 9; i++ )); do 	# EPA loop first
	for (( j = 0; j < 20; j++ )); do # standard seed loop second
		java affectiveprisonersdilemma.Population -seed ${SEEDS[${j}]} -EPA ${EPA_SEEDS[${i}]} -output out.csv -until 5000
	done
done