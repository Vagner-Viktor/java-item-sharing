delete from users;
delete from requests;
delete from items;

merge into users (id, name, email)
values(1, 'TestUserNameInBD', 'TestUserEmail@in.BD');

merge into users (id, name, email)
values(2, 'TestUserNameInBD2', 'TestUserEmail2@in.BD');

merge into requests(id, description, requestor_id, created)
values (1, 'TestDescriptionInDB', 1, '2024-07-30 06:00:00');

merge into items(id, name, description, is_available, owner_id, request_id)
values (1, 'TestNameInBD', 'TestDescriptionInDB', true, 1, 1);