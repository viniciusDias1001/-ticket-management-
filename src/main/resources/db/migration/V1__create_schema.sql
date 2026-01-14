-- USERS
create table if not exists users (
  id uuid primary key,
  name varchar(120) not null,
  email varchar(180) not null unique,
  password_hash varchar(120) not null,
  role varchar(30) not null,
  created_at timestamp with time zone not null,
  updated_at timestamp with time zone not null
);

-- TICKETS
create table if not exists tickets (
  id uuid primary key,
  title varchar(160) not null,
  description text not null,
  status varchar(30) not null,
  priority varchar(30) not null,
  created_by_id uuid not null,
  assigned_to_id uuid null,
  created_at timestamp with time zone not null,
  updated_at timestamp with time zone not null,

  constraint fk_ticket_created_by
    foreign key (created_by_id) references users(id),

  constraint fk_ticket_assigned_to
    foreign key (assigned_to_id) references users(id)
);

-- TICKET_HISTORY
create table if not exists ticket_history (
  id uuid primary key,
  ticket_id uuid not null,
  performed_by_id uuid null,
  action varchar(60) not null,
  from_status varchar(30) null,
  to_status varchar(30) null,
  notes varchar(255) null,
  created_at timestamp with time zone not null,

  constraint fk_history_ticket
    foreign key (ticket_id) references tickets(id),

  constraint fk_history_performed_by
    foreign key (performed_by_id) references users(id)
);

-- Índices (como os @Index do seu Ticket)
create index if not exists ix_ticket_status on tickets(status);
create index if not exists ix_ticket_priority on tickets(priority);
create index if not exists ix_ticket_created_by on tickets(created_by_id);
create index if not exists ix_ticket_assigned_to on tickets(assigned_to_id);

create index if not exists ix_history_ticket_created_at on ticket_history(ticket_id, created_at desc);
