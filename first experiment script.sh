#! /bin/bash
cd ~/NetBeansProjects/AffectivePrisonersDilemma/build/classes

EPA_COUNT=1
EPA_SEEDS=()
until [[ $EPA_COUNT -gt 3 ]]; do
	EPA_SEEDS+=($RANDOM)
	let EPA_COUNT=EPA_COUNT+1
done

echo "${EPA_SEEDS[*]}"

SEED_COUNT=1
SEEDS=()
until [[ $SEED_COUNT -gt 3 ]]; do
	SEEDS+=($RANDOM)
	let SEED_COUNT=SEED_COUNT+1
done
echo "${SEEDS[*]}"

for (( i = 0; i < 3; i++ )); do 	# EPA loop first
	for (( j = 0; j < 3; j++ )); do # standard seed loop second
		java affectiveprisonersdilemma.Population -seed ${SEEDS[${j}]} -EPA ${EPA_SEEDS[${i}]} -output out.csv -until 500
	done
done