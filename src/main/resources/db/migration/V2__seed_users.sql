-- ADMIN
insert into users (id, name, email, password_hash, role, created_at, updated_at)
values (
  'a9292d58-5e3c-4819-ab6f-f01d838cf55d',
  'Admin',
  'vinicius@local.com',
  '$2b$10$VfgkAd8U2GKpjoN.3suwkef0VrL1KtAoWXOZnAQ8jeMA7wHjMGOWO',
  'ADMIN',
  now(),
  now()
)
on conflict (email) do nothing;

-- REVIEWER (BBG)
insert into users (id, name, email, password_hash, role, created_at, updated_at)
values (
  'b7a3c2dd-cc3f-4a48-a1a3-1c2d3e4f5a6b',
  'BBG Reviewer',
  'reviewer@bbgtelecom.com',
  '$2b$10$dG28bbBjhA1hQKiu4V3gLObImEcGagapcpFUp8FtV9YHIsJxb.G6u',
  'ADMIN',
  now(),
  now()
)
on conflict (email) do nothing;
