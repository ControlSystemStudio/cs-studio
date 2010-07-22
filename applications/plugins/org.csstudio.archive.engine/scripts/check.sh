for i in 4501 4502 4503 4504 4505 4506 4507 4508 4509 4510 4511 4512 4513 4514 4515 4516 4517 4518 4519 4520 4521 4522 4523 4524 4525 4526 4527 4528 4529  4530 4531 4532 4533 4534
do
  lynx -dump http://localhost:$i/main | head -19 | grep -v "Archive Engine" | grep -v Summary | grep -v Version | grep -v Batch | grep -v Workspace
done
